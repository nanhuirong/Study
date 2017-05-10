package com.huirong.spark.graphx1

import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph

import scala.reflect.ClassTag

/**
  * Created by huirong on 17-5-5.
  */
class LouvainHarness(minProgress: Int, progressCounter: Int) {
  def run[VD: ClassTag](sc: SparkContext, graph: Graph[VD, Long]) = {
    var louvainGraph = LouvainCore.createLouvainGraph(graph)
    //图被压缩的次数
    var level = -1
    //当前社区的模块度
    var q = -1.0
    //算法结束标志
    var halt = false
    do {
      level += 1
      //计算每一个节点对应的最佳社区
      val (currentQ, currentGraph, passes) = LouvainCore
        .louvain(sc, louvainGraph, minProgress, progressCounter)
      louvainGraph.unpersistVertices(blocking = false)
      louvainGraph = currentGraph
      saveLevel(sc, level, currentQ, louvainGraph)

      //如果模块度的增量超过0.001，说明当前划分比前一次好，继续迭代
      //如果计算当前社区的迭代次数小于3,停止循环
      if (passes > 2 && currentQ > q + 0.001){
        q = currentQ
        louvainGraph = LouvainCore.compressGraph(louvainGraph)
      }else{
        halt = true
      }
    }while(!halt)
    finalSave(sc, level, q, louvainGraph)
  }

  def saveLevel(sc: SparkContext, level: Int, q: Double, graph: Graph[VertexState, Long]) = {

  }

  def finalSave(sc: SparkContext, level: Int, q: Double, graph: Graph[VertexState, Long]) = {

  }


}
