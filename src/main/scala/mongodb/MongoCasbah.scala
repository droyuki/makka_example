package mongodb

import java.text.{SimpleDateFormat, DateFormat}
import scala.collection.JavaConversions._
import com.mongodb.MongoClient
import org.bson.Document
import com.mongodb.client.model.Filters

/**
 * Created by WanEnFu on 16/4/12.
 */

class MongoCasbah() {
  val mongoClient = new MongoClient("140.119.19.21", 27017)

  // 抓出所有大模型標誌
  def query(fundIds: List[String], databaseName:String, startDate: String, endDate: String): List[List[Double]] = {
    val db = mongoClient.getDatabase(databaseName)
    val fundsPrice = fundIds.map(e => {
      db.getCollection(e).find(Filters.and(Filters.gt("日期", startDate), Filters.lte("日期", endDate)))
        .iterator()
        .map(e => {
          e.get("淨值").asInstanceOf[Double]
        }).toList
    })

    fundsPrice
  }

  def close(): Unit = {
    mongoClient.close()
  }

}