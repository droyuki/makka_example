package timeSeriesPredict

import mongodb.MongoCasbah

/**
 * Created by WanEnFu on 16/7/27.
 */
object Test {

  def main(args: Array[String]): Unit = {
    val a = List("FOGBR05U8B")
    val fundsPrice = new MongoCasbah().query(a, "fund20160414", "2010/01/01", "2014/12/31")
    val result = fundsPrice.map(e => {
      val ts = e.toArray
      val tsp = new Tsp()
      val d = tsp.unitRootTest(ts) // diff number
      val pacfAcf = tsp.estimateAcfPacf(ts, d) // pacf and acf

      val models = tsp.createModel(pacfAcf, ts)
      println(models.length)
//
      models.foreach(a => {
        a.foreach(b => {
          print(b+",")
        })
        println("")
      })

      models
    })
  }

}
