package com.huirong.spark.graphx

import org.apache.spark.SparkContext

import scala.reflect.ClassTag
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.collection.mutable.{HashMap, HashSet}

/**
  * Created by huirong on 17-5-3.
  */
object Louvain {

  //创建图
  def createLouvainGraph[VD: ClassTag](initGraph: Graph[VD, Double]): Graph[VertexData, Double] = {
    val nodeWeights: VertexRDD[Double] = initGraph.aggregateMessages(
      trip => {
        trip.sendToDst(trip.attr)
        trip.sendToSrc(trip.attr)
      },
      (a, b) => a + b
    )
    val LouvainGraph = initGraph.outerJoinVertices(nodeWeights)((vid, oldData, opt) =>{
      val data = new VertexData
      val weight = opt.getOrElse(0.0)
      data.cId = vid
      data.degree = weight
      data.innerVertices += vid
      data.commVertices += vid
      data
    })
    LouvainGraph
  }

  //更新图
  def updateGraph(G: Graph[VertexData, Double],
                  changeInfo: RDD[(VertexId, Long)]): Graph[VertexData, Double] = {
    var newGraph = G.joinVertices(changeInfo)((vid, oldData, newCid) =>{
      val data = new VertexData
      data.innerDegree = oldData.innerDegree
      data.innerVertices = oldData.innerVertices
      data.cId = newCid
      data.degree = oldData.degree
      data
    })
    val updateInfo = newGraph.vertices.map(x => {
      val vid = x._1
      val cid = x._2.cId
      (vid, cid)
    }).groupByKey().flatMap(x => {
      val vertices = x._2
      vertices.map(vid => (vid, vertices))
    })
    newGraph = newGraph.joinVertices(updateInfo)((vid, oldData, opt) => {
      val data = new VertexData
      data.cId = oldData.cId
      val cVertices = new HashSet[VertexId]()
      for (vid <- opt)
        cVertices += vid
      data.innerDegree = oldData.innerDegree
      data.innerVertices = oldData.innerVertices
      data.degree = oldData.degree
      data.commVertices = cVertices
      data
    })
    newGraph
  }

  def getCommunities(G: Graph[VertexData, Double]) = {
    val communities = G.vertices.map(x => {
      val innerVertices = x._2.innerVertices
      val cid = x._2.cId
      (cid, innerVertices)
    }).groupByKey().map(x => {
      val cid = x._1
      val vertices = x._2.flatten.toSet
      (cid, vertices)
    })
//    communities.foreach(line => println(s"${line} 结果"))
    communities
  }

  def execute[VD: ClassTag](sc: SparkContext, initGraph: Graph[VD, Double]) = {
    var louvainGraph = createLouvainGraph(initGraph)
    println(s"初始图节点数${louvainGraph.vertices.count()} 边数${louvainGraph.edges.count()}")
    //计算所有节点的度，模块度中对应m
    val m = sc.broadcast(louvainGraph.edges.map(e => e.attr).sum())
    println(s"所有节点的度${m.value}")
    var iteration: Int = 0
//    getNeighborCommInfo(louvainGraph)
    var res = step1(louvainGraph, m.value)
    while (res._2 != 0 && iteration < 20){
      louvainGraph = res._1
      louvainGraph = step2(louvainGraph)
      getCommunities(louvainGraph)
      res = step1(louvainGraph, m.value)
      iteration += 1
    }
    getCommunities(louvainGraph)
  }

  /**
    * 算法第一步
    * 不断地遍历网络中的结点，尝试将单个结点加入能够使modularity提升最大的社区中，直到所有结点都不再变化
    */
  def step1(louvainGraph: Graph[VertexData, Double], m: Double): (Graph[VertexData, Double], Int) = {
    var G = louvainGraph
    var iteration = 0
    var canStop = false
    while (iteration < 100 && !canStop){
      val neighborComm = getNeighborCommInfo(G)
      val changeInfo = getChangeInfo(G, neighborComm, m)
      //计算尝试分配后顶点社区发生变化的个数
      val changeCount = G.vertices.zip(changeInfo).filter(x =>
        x._1._2.cId != x._2._2
      ).count()

      val newChangeInfo = Graph
        .fromEdgeTuples(changeInfo.map(x => (x._1, x._2)), 0)
        .connectedComponents()
        .vertices
      G = updateGraph(G, newChangeInfo)
      if (changeCount == 0){
        canStop = true
      }else{
        val newChangeInfo = Graph
          .fromEdgeTuples(changeInfo.map(x => (x._1, x._2)), 0)
          .connectedComponents()
          .vertices
        G = updateGraph(G, newChangeInfo)
        iteration += 1
      }
    }
    (G, iteration)
  }

  /**
    * 算法第二步，将一个个小的社区归并为一个超结点来重新构造网络，这时边的权重为两个结点内所有原始结点的边权重之和
    */
  def step2(G: Graph[VertexData, Double]): Graph[VertexData, Double] = {
    val edges = G.triplets.filter(trip => trip.srcAttr.cId != trip.dstAttr.cId).map( trip => {
      val cid1 = trip.srcAttr.cId
      val cid2 = trip.dstAttr.cId
      val weight = trip.attr
      ((math.min(cid1, cid2), math.max(cid1, cid2)), weight)
    }).groupByKey().map(x =>Edge(x._1._1, x._1._2, x._2.sum))
    val initG = Graph.fromEdges(edges, None)
    var louvainGraph = createLouvainGraph(initG)

    val oldKIn = G.vertices.map(v => (v._2.cId, (v._2.innerVertices, v._2.innerDegree)))
      .groupByKey()
      .map(x => {
        val cid = x._1
        val vertices = x._2.flatMap(t => t._1).toSet[VertexId]
        val kIn = x._2.map( t => t._2).sum
        (cid, (vertices, kIn))
      })

    val newKIn = G.triplets.filter(trip => trip.srcAttr.cId == trip.dstAttr.cId)
      .map(trip => {
        val cid = trip.srcAttr.cId
        val vertices1 = trip.srcAttr.innerVertices
        val vertices2 = trip.dstAttr.innerVertices
        val weight = trip.attr * 2
        (cid, (vertices1.union(vertices2), weight))
      }).groupByKey().map(x => {
      val cid = x._1
      val vertices = x._2.flatMap(t => t._1).toSet[VertexId]
      val kIn = x._2.map(t => t._2).sum
      (cid, (vertices, kIn))
    })

    val superVertexInfo = oldKIn.union(newKIn).groupByKey().map(x => {
      val cid = x._1
      val vertices = x._2.flatMap(t => t._1).toSet[VertexId]
      val kIn = x._2.map(t => t._2).sum
      (cid, (vertices, kIn))
    })
    louvainGraph = louvainGraph.outerJoinVertices(superVertexInfo)((vid, oldData, opt) => {
      val innerVertices = new HashSet[VertexId]()
      val kIn = opt.get._2
      for (vid <- opt.get._1){
        innerVertices += vid
      }
      oldData.innerVertices = innerVertices
      oldData.innerDegree = kIn
      oldData
    })
    louvainGraph
  }

  def getChangeInfo(G: Graph[VertexData, Double],
                    neighborCommInfo: RDD[(VertexId, Iterable[(Long, Double, Double)])],
                    m: Double): RDD[(VertexId, Long, Double)] = {
    val changeInfo = G.vertices.join(neighborCommInfo).map(x => {
      val vid = x._1
      val data = x._2._1
      val commIter = x._2._2
      val vCid = data.cId
      val k_v = data.degree + data.innerDegree
      //将顶点尝试分配到所有的邻接社区，计算模块度的大小
      val maxQ = commIter.map(t => {
        val nCid = t._1
        val k_v_in = t._2
        var tot = t._3
        if (vCid == nCid)
          tot -= k_v
        val q = (k_v_in - tot * k_v / m)
        (vid, nCid, q)
      }).max(Ordering.by[(VertexId, Long, Double), Double](_._3))

      if (maxQ._3 > 0.0) maxQ else (vid, vCid, 0.0)
    })
    changeInfo
  }

  //求出每个顶点的所有邻接邻接社区，顶点的权重以及邻接社区的权重
  def getNeighborCommInfo(G: Graph[VertexData, Double]): RDD[(VertexId,
    Iterable[(Long, Double, Double)])] = {
    //求每有一个社区对应的度 [cid, weight]
    val commTot = G.vertices
      .map(v => (v._2.cId, v._2.degree + v._2.innerDegree))
        .reduceByKey(_ + _)

//      .groupByKey()
//      .map(x => {
//        val cid = x._1
//        val tot = x._2.sum
//        (cid, tot)
//      })

    //求每一个社区对应的邻接顶点[cid, HashMap[VertexId, weight]]
    val commKIN = G.triplets.flatMap(trip => {
      val weight = trip.attr
      Array((trip.srcAttr.cId, (trip.dstId, weight)),
        (trip.dstAttr.cId, (trip.srcId, weight)))
    }).groupByKey().map(t => {
      val cid = t._1
      val m = new HashMap[VertexId, Double]()
      for (x <- t._2){
        if (m.contains(x._1))
          m(x._1) += x._2
        else
          m(x._1) = x._2
      }
      (cid, m)
    })

    //[VertexId, (cid, )]
    val neighborCommInfo = commTot.join(commKIN).flatMap(x => {
      val cid = x._1
      val tot = x._2._1
      x._2._2.map( t => {
        val vid = t._1
        val k_in = t._2
        (vid, (cid, k_in, tot))
      })
    }).groupByKey()
    println(s"求出每个顶点的所有邻接邻接社区，顶点的权重以及邻接社区的权重")
    println(s"${neighborCommInfo.count()}")
    neighborCommInfo
  }
}
