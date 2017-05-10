package com.huirong.spark.graphx

import edu.umd.cloud9.collection.XMLInputFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._

import scala.xml._
import com.google.common.hash.Hashing
import org.apache.spark.util.StatCounter


/**
  * Created by huirong on 17-5-3.
  */
object RunGraphx {
  def main(args: Array[String]): Unit ={
    val path = "file:///home/huirong/graph/medline"
    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("RunGraphx")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)
    val medlineRaw = loadMedline(sc, path)
    val medline: RDD[Seq[String]] = medlineRaw.map(majorTopics).cache()

    //计算基本信息
    val topics = medline.flatMap(topics => topics)
    val topicCounts = topics.countByValue()
    println(s"文章数目：${medline.count()} 主题数目: ${topicCounts.size}")
    val tcSeq = topicCounts.toSeq
    tcSeq.sortBy(_._2).reverse.take(10).foreach(tc => println(tc._1 + "->" + tc._2))

    //生成伴生关系
    val topicPairs: RDD[Seq[String]] = medline.flatMap(t => t.sorted.combinations(2))
    //伴生关系统计输出
    val cooccurs: RDD[(Seq[String], Int)] = topicPairs.map(pair => (pair, 1)).reduceByKey(_+_)
    println(s"伴生关系：${cooccurs.count()}")
    val order = Ordering.by[(Seq[String], Int), Int](_._2)
    cooccurs.top(10)(order).foreach(cooccur => println(s"${cooccur._1} -> ${cooccur._2}"))

    //验证hash算法是否有效
    val vertices = topics.map(topic => (hashId(topic), topic))
    val uniqueHash = vertices.map(_._1).countByValue()
    val uniqueTopic = vertices.map(_._2).countByValue()
    println(s"hash:${uniqueHash.size}\ttopic:${uniqueTopic.size}")

    //生成图
    val edges = cooccurs.map{ cooccur =>
      val (topics, count) = cooccur
      val ids = topics.map(hashId).sorted
      Edge(ids(0), ids(1), count)
      //边包含源ID和目标ID，并且srcID < destID,和边属性（可以为任意值）
    }
    //会自动进行定点去重，如果edgeRDD中有重复出现的的顶点二元组，不会进行去重
    val topicGraph = Graph(vertices, edges)
    topicGraph.cache()
    //联通组件, VertexID定点所属联通组件的标识
    val connectedComponentGraph: Graph[VertexId, Int] = topicGraph.connectedComponents()
    val componentCounts = sortedConnectedComponents(connectedComponentGraph)
    println(s"联通组件${componentCounts.size}")
    componentCounts.take(10).foreach(println)

    //类似于join
    val nameId = topicGraph.vertices.
      innerJoin(connectedComponentGraph.vertices){
        (topicId, name, connectId) => (name, connectId)
      }

    //统计顶点的度
    val degrees: VertexRDD[Int] = topicGraph.degrees
    val stat: StatCounter = degrees.map(_._2).stats()
    println(s"度的分布${stat}")

    //将没有意义的伴生二元组去除,利用皮尔逊卡方测试
    val T = medline.count()
    val topicCountRDD = topics.map(topic => (hashId(topic), 1)).reduceByKey(_+_)
    val graph = Graph(topicCountRDD, topicGraph.edges)
    //转换边的值
    val chiSqGraphx = graph.mapTriplets(triplet => {
      chiSq(triplet.attr, triplet.srcAttr, triplet.dstAttr, T)
    })
    //去除噪声
    val interesting = chiSqGraphx.subgraph(triplet => triplet.attr > 19.5)

    //寻找系,定点包含每个每顶点的三角形计数
    val triGraph = interesting.triangleCount()
    //计算局部聚类系数
    val maxTrixGraph = degrees.mapValues(count => count * (count - 1) / 2.0)
    val cluster = triGraph.vertices.innerJoin(maxTrixGraph){
      (vertexId, triCount, maxTrix) => {
        if (maxTrix == 0) 0 else triCount / maxTrix
      }
    }
    val mean = cluster.map(_._2).sum / interesting.vertices.count()
    println(s"平均聚类系数:${mean}")
    //计算平均路径长度
    val mapGraph = interesting.mapVertices((id, _) => {
      Map(id -> 0)
    })
    val start = Map[VertexId, Int]()
    val res = mapGraph.pregel(start)(update, iterate, mergeMaps)
    val paths = res.vertices.flatMap{ case (id, m) =>
        m.map{case (k, v) =>
            if (id < k)
              (id, k, v)
            else {
              (k, id, v)
            }

        }
    }.distinct()
    println(s"路径${paths.map(_._3).filter(_ > 0).stats()}")
    sc.stop()
  }


  def update(id: VertexId,
             state: Map[VertexId, Int],
             msg: Map[VertexId, Int]) = {
    mergeMaps(state, msg)
  }
  //将新消息中的信息合并到顶点状态，
  def mergeMaps(m1: Map[VertexId, Int],
                m2: Map[VertexId, Int]): Map[VertexId, Int] = {

    def minThatExists(k: VertexId): Int = {
      math.min(
        m1.getOrElse(k, Int.MaxValue),
        m2.getOrElse(k, Int.MaxValue)
      )
    }

    (m1.keySet ++ m2.keySet).map{
      k => (k, minThatExists(k))
    }.toMap
  }
  //reduce阶段
  def checkIncrement(a: Map[VertexId, Int],
                     b:Map[VertexId, Int],
                     bid: VertexId) = {
    val aplus = a.map{
      case(v, d) => v -> (d + 1)
    }
    if (b !=mergeMaps(aplus, b)){
      //如果结果不同，就将结果发送给邻接点
      Iterator((bid, aplus))
    }else{
      Iterator.empty
    }
  }

  //在每一次迭代中，对定点进行更新操作
  def iterate(e: EdgeTriplet[Map[VertexId, Int], _]) = {
    checkIncrement(e.srcAttr, e.dstAttr, e.dstId) ++
    checkIncrement(e.dstAttr, e.srcAttr, e.srcId)
  }

  def chiSq(YY: Int, YB: Int, YA: Int, T: Long): Double = {
    val NB = T - YB
    val NA = T - YA
    val YN = YA - YY
    val NY = YB - YY
    val NN = T - NY - YN - YY
    val inner = (YY * NN - YN * NY)
    T * math.pow(inner, 2) / (YA * NA * YB * NB)
  }

  //列出所有的联通组件并按照大小排序
  def sortedConnectedComponents(connectedComponentGraph:
                                Graph[VertexId, Int]): Seq[(VertexId, Long)] = {
    val componentCounts = connectedComponentGraph
      .vertices
      .map(_._2)
      .countByValue()
    componentCounts.toSeq.sortBy(_._2).reverse
  }

  //为每一个主题生成一个64位标识
  def hashId(str: String):Long = {
    Hashing.md5().hashString(str).asLong()
  }

  def loadMedline(sc: SparkContext, path: String): RDD[String] ={
    val conf = new Configuration()
    conf.set(XMLInputFormat.START_TAG_KEY, "<MedlineCitation ")
    conf.set(XMLInputFormat.END_TAG_KEY, "</MedlineCitation>")
    val in = sc.newAPIHadoopFile(path, classOf[XMLInputFormat],
      classOf[LongWritable], classOf[Text], conf)
    in.map(line => line._2.toString)
  }

  //选取文章的关键主题
  def majorTopics(record: String): Seq[String] = {
    val elem = XML.loadString(record)
    val dn = elem \\ "DescriptorName"
    val mt = dn.filter(n => (n \ "@MajorTopicYN").text == "Y")
    mt.map(n => n.text)
  }

}
