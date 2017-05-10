package com.huirong.spark.graphx1

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.reflect.ClassTag
import org.apache.spark.graphx._

import scala.collection.mutable.HashMap

/**
  * Created by huirong on 17-5-5.
  */
object LouvainCore {
  def createLouvainGraph[VD: ClassTag](graph: Graph[VD, Long]): Graph[VertexState, Long] = {
    val nodeWeightMapFunc = (e: EdgeTriplet[VD, Long]) =>
      Iterator((e.srcId, e.attr), (e.dstId, e.attr))
    val nodeWeightReduceFunc = (e1: Long, e2: Long) => e1 + e2
    val nodeWeights = graph.mapReduceTriplets(nodeWeightMapFunc,
      nodeWeightReduceFunc)

    val louvainGraph = graph.outerJoinVertices(nodeWeights)((vid, data, weightOption) => {
      val weight: Long = weightOption.getOrElse(0L)
      val state = new VertexState
      state.community = vid
      state.changed = false
      state.communitySigmaTot = weight
      state.internalWeight = 0L
      state.nodeWeight = weight
      state
    }).partitionBy(PartitionStrategy.EdgePartition2D)
      .groupEdges(_ + _)
    louvainGraph
  }

  /**
    * 尝试将所有顶点的社区进行重新划分
    * @param sc
    * @param graph
    * @param minProgress
    * @param progressCounter
    * @return (当前的模块度，新图，迭代次数)
    */
  def louvain(sc: SparkContext,
              graph: Graph[VertexState, Long],
              minProgress: Int = 1,
              progressCounter: Int = 1): (Double, Graph[VertexState, Long], Int) = {
    var louvainGraph = graph.cache()
    val graphWeight = louvainGraph.vertices.values
      .map(data => data.internalWeight + data.nodeWeight)
        .reduce(_ + _)
    var totalGraphWeight = sc.broadcast(graphWeight)
    //得到定点的临界社区
    var msgRDD = louvainGraph.mapReduceTriplets(sendMsg, mergeMsg)
    var activeMessage = msgRDD.count()
    var updated =0L - minProgress
    var even = false
    var count = 0
    val maxIteration = 100000
    var stop = 0
    var updateLastPhase = 0L
    do {
      count += 1
      even = !even
      //计算当前顶点的最佳社区
      val labeledVerts = louVainVertJoin(louvainGraph, msgRDD, totalGraphWeight, even).cache()
      //统计所有社区的权重
      val communityUpdate = labeledVerts.map({ case (vid, data) =>
        (data.community, data.nodeWeight + data.internalWeight)
      }).reduceByKey(_ + _)
      //将节点id与新社区id、权重关联
      val communityMapping = labeledVerts.map({case (vid, data) => (data.community, vid)})
        .join(communityUpdate)
        .map({case (community, (vid, sigmaTot)) => (vid, (community, sigmaTot))})
      //更新节点的信息
      val updatedVerts = labeledVerts.join(communityMapping)
        .map({case (vid, (data, communityTuple)) =>
            data.community = communityTuple._1
            data.communitySigmaTot = communityTuple._2
          (vid, data)
        })
      val preG = louvainGraph
      louvainGraph = louvainGraph.outerJoinVertices(updatedVerts)((vid, old, newOpt) =>
        newOpt.getOrElse(old)
      )
      val oldMsg = msgRDD
      msgRDD = louvainGraph.mapReduceTriplets(sendMsg, mergeMsg)
      activeMessage = msgRDD.count()
      if (even)
        updated = 0
      if (!even){
        if(updated >= updateLastPhase - minProgress)
          stop += 1
      }
    }while(stop < progressCounter && (even || (updated > 0 && count < maxIteration)))
    //更新计算图的整体模块度
    val newVerts = louvainGraph.vertices.innerJoin(msgRDD)((vid,vdata,msgs)=> {
      // sum the nodes internal weight and all of its edges that are in its community
      val community = vdata.community
      var k_i_in = vdata.internalWeight
      var sigmaTot = vdata.communitySigmaTot.toDouble
      msgs.foreach({ case( (communityId,sigmaTotal),communityEdgeWeight ) =>
        if (vdata.community == communityId) k_i_in += communityEdgeWeight})
      val M = totalGraphWeight.value
      val k_i = vdata.nodeWeight + vdata.internalWeight
      var q = (k_i_in.toDouble / M) -  ( ( sigmaTot *k_i) / math.pow(M, 2) )
      //println(s"vid: $vid community: $community $q = ($k_i_in / $M) -  ( ($sigmaTot * $k_i) / math.pow($M, 2) )")
      if (q < 0) 0 else q
    })
    val actualQ = newVerts.values.reduce(_+_)

    // return the modularity value of the graph along with the
    // graph. vertices are labeled with their community
    return (actualQ,louvainGraph,count/2)

  }

  private def louVainVertJoin(louvainGraph: Graph[VertexState, Long],
                              msgRDD: VertexRDD[Map[(Long, Long), Long]],
                              totalEdgeWeight: Broadcast[Long],
                              even: Boolean) = {
    louvainGraph.vertices.innerJoin(msgRDD)((vid, data, msgs) => {
      var bestCommnity = data.community
      var startingCommnityId = bestCommnity
      var maxDeltaQ = BigDecimal(0.0)
      var bestSigmaTot = 0L
      msgs.foreach({case ((communityId, sigmaTotal), communityEdgeWeight) =>
          val deltaQ = q(startingCommnityId, communityId, sigmaTotal,
            communityEdgeWeight, data.nodeWeight, data.internalWeight, totalEdgeWeight.value)
          if (deltaQ > maxDeltaQ ||
            (deltaQ > 0 && (deltaQ == maxDeltaQ && communityId > bestCommnity))){
            maxDeltaQ = deltaQ
            bestCommnity = communityId
            bestSigmaTot = sigmaTotal
          }
      })
      if (data.community != bestCommnity &&
        ((even && data.community > bestCommnity) || (!even && data.community < bestCommnity))) {
        data.community = bestCommnity
        data.communitySigmaTot = bestSigmaTot
        data.changed = true
      }else{
        data.changed = false
      }
      data
    })
  }

  private def q(currCommunityId:Long, testCommunityId:Long, testSigmaTot:Long,
                edgeWeightInCommunity:Long, nodeWeight:Long,
                internalWeight:Long, totalEdgeWeight:Long) : BigDecimal = {
    val isCurrentCommunity = (currCommunityId.equals(testCommunityId))
    val M = BigDecimal(totalEdgeWeight)
    val k_i_in_L =  if (isCurrentCommunity)
      edgeWeightInCommunity + internalWeight
    else
      edgeWeightInCommunity
    val k_i_in = BigDecimal(k_i_in_L)
    val k_i = BigDecimal(nodeWeight + internalWeight)
    val sigma_tot = if (isCurrentCommunity)
      BigDecimal(testSigmaTot) - k_i
    else
      BigDecimal(testSigmaTot)

    var deltaQ =  BigDecimal(0.0)
    if (!(isCurrentCommunity && sigma_tot.equals(0.0))) {
      deltaQ = k_i_in - ( k_i * sigma_tot / M)
    }
    deltaQ
  }

  private def sendMsg(et: EdgeTriplet[VertexState, Long]) = {
    val m1 = (et.dstId, Map((et.srcAttr.community, et.srcAttr.communitySigmaTot) -> et.attr))
    val m2 = (et.srcId, Map((et.dstAttr.community, et.dstAttr.communitySigmaTot) -> et.attr))
    Iterator(m1, m2)
  }

  private def mergeMsg(m1: Map[(Long, Long), Long], m2: Map[(Long, Long), Long]) = {
    val newMap = HashMap[(Long, Long), Long]()
    m1.foreach({ case(k, v) =>
      if (newMap.contains(k))
        newMap(k) += v
      else
        newMap(k) = v
    })
    m2.foreach({ case(k, v) =>
      if (newMap.contains(k))
        newMap(k) += v
      else
        newMap(k) = v
    })
    newMap.toMap
  }

  def compressGraph(graph: Graph[VertexState, Long],
                    debug: Boolean = true): Graph[VertexState, Long] = {
    // aggregate the edge weights of self loops. edges with both src and dst in the same community.
    // WARNING  can not use graph.mapReduceTriplets because we are mapping to new vertexIds
    val internalEdgeWeights = graph.triplets.flatMap(et=>{
      if (et.srcAttr.community == et.dstAttr.community){
        Iterator( ( et.srcAttr.community, 2*et.attr) )
      }
      else Iterator.empty
    }).reduceByKey(_+_)


    // aggregate the internal weights of all nodes in each community
    var internalWeights = graph.vertices.values
      .map(vdata=> (vdata.community,vdata.internalWeight))
      .reduceByKey(_+_)

    // join internal weights and self edges to find new interal weight of each community
    val newVerts = internalWeights.leftOuterJoin(internalEdgeWeights)
      .map({case (vid,(weight1,weight2Option)) =>
      val weight2 = weight2Option.getOrElse(0L)
      val state = new VertexState()
      state.community = vid
      state.changed = false
      state.communitySigmaTot = 0L
      state.internalWeight = weight1+weight2
      state.nodeWeight = 0L
      (vid,state)
    })


    // translate each vertex edge to a community edge
    val edges = graph.triplets.flatMap(et=> {
      val src = math.min(et.srcAttr.community,et.dstAttr.community)
      val dst = math.max(et.srcAttr.community,et.dstAttr.community)
      if (src != dst) Iterator(new Edge(src, dst, et.attr))
      else Iterator.empty
    }).cache()


    // generate a new graph where each community of the previous
    // graph is now represented as a single vertex
    val compressedGraph = Graph(newVerts,edges)
      .partitionBy(PartitionStrategy.EdgePartition2D).groupEdges(_+_)

    // calculate the weighted degree of each node
    val nodeWeightMapFunc = (e:EdgeTriplet[VertexState,Long]) => Iterator((e.srcId,e.attr), (e.dstId,e.attr))
    val nodeWeightReduceFunc = (e1:Long,e2:Long) => e1+e2
    val nodeWeights = compressedGraph.mapReduceTriplets(nodeWeightMapFunc,nodeWeightReduceFunc)

    // fill in the weighted degree of each node
    // val louvainGraph = compressedGraph.joinVertices(nodeWeights)((vid,data,weight)=> {
    val louvainGraph = compressedGraph.outerJoinVertices(nodeWeights)((vid,data,weightOption)=> {
      val weight = weightOption.getOrElse(0L)
      data.communitySigmaTot = weight +data.internalWeight
      data.nodeWeight = weight
      data
    })
    return louvainGraph
  }


}
