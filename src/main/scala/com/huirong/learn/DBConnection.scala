package com.huirong.learn

/**
  * Created by huirong on 17-4-17.
  */
class DBConnection {
  private val props = Map(
    "url" -> DBConnection.db_url,
    "user" -> DBConnection.db_user,
    "pass" -> DBConnection.db_pass
  )
  println(s"created new connection for " + props("url"))
}
object DBConnection{
  private val db_url = "jdbc:localhost"
  private val db_user = "franken"
  private val db_pass = "berry"

  def apply(): DBConnection = new DBConnection()
}
