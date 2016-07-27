package util

import java.util.Calendar

import play.api.libs.json.{JsValue, Json}

/**
  * Created by WeiChen on 2016/7/27.
  */
object Test {
  def main(args: Array[String]): Unit = {
    val in = "{\"topic\":\"/fund/tsp/res\",\"data\":{\"k\":\"v\"}}"

    test(in)
  }

  def test(data:String): Unit ={
    val raw = Json.parse(data)
    val topic = (raw \ "topic").as[String]
    val response = Json.stringify((raw \ "data").get)
    println(topic)
    println(response)
  }

}
