package timeSeriesPredict

import com.oring.smartcity.makka.Job

import play.api.libs.json.Json

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

//    val tsp = new Tsp()
//    val fundsPrice = new MongoCasbah().query(portfolio_fundname, "fund20160414")
//    tspRequestMap.put(requestId, tsp)


  }

}
