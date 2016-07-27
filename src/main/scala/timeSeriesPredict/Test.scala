package timeSeriesPredict

import mongodb.MongoCasbah

/**
 * Created by WanEnFu on 16/7/27.
 */
object Test {

  def main(args: Array[String]): Unit = {
    val a = List("FOGBR05U8B")
    val fundsPrice = new MongoCasbah().query(a, "fund20160414", "2010/01/01", "2014/12/31")
    fundsPrice.foreach (e1 => {
      e1.foreach (e2 => println (e2 + ",") )
      println ("")
    })

  }

}
