package com.huirong.spark.graphx

import scala.collection.mutable.HashSet


/**
  * Created by huirong on 17-5-3.
  */
class VertexData extends Serializable{
  var cId: Long = -1L
  //内部节点的权重
  var innerDegree = 0.0
  //内部节点
  var innerVertices = new HashSet[Long]()
  var degree = 0.0
  var commVertices = new HashSet[Long]()

}
