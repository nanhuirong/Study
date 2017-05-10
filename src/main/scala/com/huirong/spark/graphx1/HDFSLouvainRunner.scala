package com.huirong.spark.graphx1
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph

/**
  * Created by huirong on 17-5-5.
  */
class HDFSLouvainRunner(minProgress: Int, progressCounter: Int, outDir: String)
  extends LouvainHarness(minProgress:Int,progressCounter:Int){
  var qValues = Array[(Int, Double)]()
  override def saveLevel(sc: SparkContext, level: Int,
                         q: Double, graph: Graph[VertexState, Long]) = {
    graph.vertices
      .map{ v =>
        IpAddress.toString(v._1) + "," + v._2.community
      }.repartition(1).saveAsTextFile(outDir + "/level_" + level + "_vertices")
    graph.edges
      .map{ e =>
        IpAddress.toString(e.srcId) + "," + IpAddress.toString(e.dstId) + "," + e.attr
      }.repartition(1).saveAsTextFile(outDir + "/level_" + level + "_edges")
    qValues = qValues :+ ((level, q))
    sc.parallelize(qValues, 1).saveAsTextFile(outDir + "/level_" + level + "_qvalues")
  }

  override def finalSave(sc: SparkContext, level: Int, q: Double, graph: Graph[VertexState, Long]) = {
    graph.vertices
      .map{ v =>
        IpAddress.toString(v._1) + "," + v._2.community
      }.repartition(1).saveAsTextFile(outDir + "/level_final_vertices")
    graph.edges
      .map{ e =>
        IpAddress.toString(e.srcId) + "," + IpAddress.toString(e.dstId) + "," + e.attr
      }.repartition(1).saveAsTextFile(outDir + "/level_final_edges")
    qValues = qValues :+ ((level, q))
    sc.parallelize(qValues, 1).saveAsTextFile(outDir + "/level_final_qvalues")
  }

}
