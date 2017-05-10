package com.huirong.spark.graphx1

/**
  * Created by huirong on 17-5-5.
  */
class VertexState extends Serializable{
  //定点所属社区ID
  var community = -1L
  //社区的度
  var communitySigmaTot = 0L

  var nodeWeight = 0L
  var internalWeight = 0L
  var changed = false

  override def toString(): String = {
    "{community:"+community+",communitySigmaTot:"+communitySigmaTot+
      ",internalWeight:"+internalWeight+",nodeWeight:"+nodeWeight+"}"
  }


}
