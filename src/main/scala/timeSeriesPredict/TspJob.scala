package timeSeriesPredict

import com.oring.smartcity.makka.{Data, Job}
import mongodb.MongoCasbah
import play.api.libs.json.{JsValue, Json}

/**
  * Created by WanEnFu on 16/7/27.
  */
class TspJob extends Job {

  val tspRequestMap = collection.mutable.Map[String, Tsp]() // key is requestId, value is Tsp Object

  override def init(): Unit = {
    // no use
  }

  override def receiveData(data: String): Unit = {
    /* Example
    { "portfolio_fundname": [ 'F0HKG062UD' ], "user": 'admin', "requestId": '287f86eb-975b-458e-a177-270a30df96ae' }
    */
    val request = Json.parse(data)
    val portfolio_fundname = ((request \ "portfolio_fundname")).as[List[String]]
    val user = ((request \ "user")).as[String]
    val requestId = ((request \ "requestId")).as[String]

    val fundsPrice = new MongoCasbah().query(portfolio_fundname, "fund20160414", "2010/01/01", "2014/12/31")
    val combine = requestId.zip(fundsPrice)

    val result = combine.map(e => {
      val id = e._1.toString
      val ts = e._2.toArray
      val tsp = new Tsp()
      val d = tsp.unitRootTest(ts) // diff number
      val pacfAcf = tsp.estimateAcfPacf(ts, d) // pacf and acf
      val models = tsp.createModel(pacfAcf, ts)
      (id, models)
    })
    val dataListJson = result.map{ p=>
      Json.obj(
        "id" -> p._1,
        p._2(0)._1.toString() -> p._2(0)._2,
        p._2(1)._1.toString() -> p._2(1)._2,
        p._2(2)._1.toString() -> p._2(2)._2
      )
    }.toList

    val resJsonObj = Json.obj(
      "topic" -> "/fund/tsp/res",
      "data" -> dataListJson,
      "requestId" -> requestId
    )

    val resJsonStr = Json.stringify(resJsonObj)
    pipe("MqttJob", new Data(resJsonStr))
  }
}
