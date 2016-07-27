package timeSeriesPredict

import org.rosuda.REngine.{RList, REXP, REXPMismatchException, REngineException}
import org.rosuda.REngine.Rserve.{RserveException, RConnection}


/**
 * Created by WanEnFu on 16/7/27.
 */
class Tsp {

  def unitRootTest(ts: Array[Double]): Int = {
    var d: Int = 0
    val rConnections = new RConnection("10.211.55.3", 6311)
    try {
      rConnections.assign("ts", ts)
      rConnections.eval("source(\"/dkShell/Rfile/univariateTimeSeriesModel.R\")")
      rConnections.eval("print(ts)")
      d = rConnections.eval("unitRootTestADF(ts)").asInteger
    } catch {
      case e: REngineException => {
        e.printStackTrace
      }
      case e: REXPMismatchException => {
        e.printStackTrace
      }
    }

    rConnections.close()
    return d
  }

  def estimateAcfPacf(ts: Array[Double], d: Int): List[ArmaParameter] = {
    val rConnections = new RConnection("10.211.55.3", 6311)
    rConnections.eval("source(\"/dkShell/Rfile/univariateTimeSeriesModel.R\")")
    var acfPacf: Array[Int] = new Array[Int](0)
    try {
      rConnections.assign("ts", ts)
      acfPacf = rConnections.eval("estimateAcfPacf(ts)").asIntegers
    }
    catch {
      case e: REXPMismatchException => {
        e.printStackTrace
      }
      case e: RserveException => {
        e.printStackTrace
      }
      case e: REngineException => {
        e.printStackTrace
      }
    }
    var i = 0;
    var j = 0;
    val acfNum: Int = acfPacf(0)
    var armaParameters = List.empty[ArmaParameter]
    for (i <- 1 to acfNum) {
      for (j <- acfNum + 1 to acfPacf.length - 1) {
        val armaParameter: ArmaParameter = new ArmaParameter(acfPacf(i), d, acfPacf(j))
        armaParameters ::= armaParameter
      }
    }

    rConnections.close()
    return armaParameters
  }

  def createModel(pacfAcf: List[ArmaParameter], ts: Array[Double]): Array[(String, List[Double])] = {
    val models = pacfAcf.map(e => {
      val arimaModel = createArima(e, ts)(0)
//      val garchModel = createGarch(e, ts)
//      (arimaModel, garchModel)
      arimaModel
    })

    var univariateModelsAll: List[UnivariateModel] = List.empty
    val modelsFlatten = models.foreach(e => {
//      univariateModelsAll ::= e._1
//      univariateModelsAll ::= e._2
      univariateModelsAll ::= e
    })
    var univariateModels = univariateModelsAll.filter(!_.isEmptyModel)
    var finalArray = List.empty[List[Double]]
    val modelsLength = univariateModels.length
    val forecastLength = univariateModels(0).getForecast.length
    for (index <- 0 to forecastLength - 1) {
      var biggest = Double.MinValue
      var smallest = Double.MaxValue
      var sum = 0.0
      for (m <- 0 to modelsLength - 1) {
//        univariateModels(m).getForecast().foreach(aaa => print(aaa))

        sum = sum + univariateModels(m).getForecast()(index)
        if (univariateModels(m).getForecast()(index) > biggest) {
          biggest = univariateModels(m).getForecast()(index)
        }

        if (univariateModels(m).getForecast()(index) < smallest) {
          smallest = univariateModels(m).getForecast()(index)
        }
      }
      println(s"fuck small : ${smallest}, big : ${biggest}, sum : ${sum}")
      finalArray ::= List(smallest, biggest, sum/modelsLength)
    }

    val keyName = Array("min", "max", "avg")
    val combine = keyName.zip(finalArray)
    combine
  }

  def createArima(aramaParameter: ArmaParameter, ts: Array[Double]): List[UnivariateModel] = {
    val rConnection = new RConnection("10.211.55.3", 6311)
    rConnection.eval("source(\"/dkShell/Rfile/univariateTimeSeriesModel.R\")")
    rConnection.eval("source(\"/dkShell/Rfile/loadpackage.R\")")

    val tsValue: Array[Double] = ts
    val arrP = Array(aramaParameter.getPacf)
    val arrD = Array(aramaParameter.getD)
    val arrQ = Array(aramaParameter.getAcf)

    var univariateModels: List[UnivariateModel] = List.empty
    try {
      rConnection.assign("ts", tsValue)
      rConnection.assign("predictData", tsValue)
      rConnection.assign("p", arrP)
      rConnection.assign("d", arrD)
      rConnection.assign("q", arrQ)
      rConnection.eval("print(ts)")
      rConnection.eval("print(p)")
      rConnection.eval("print(d)")
      rConnection.eval("print(q)")

      val models: REXP = rConnection.eval("a <- createArimaModel(ts, predictData, p, d, q)")
      rConnection.eval("print(a)")
      val totalModelNum: Int = models.length
      if (totalModelNum > 0) {
        {
          var i: Int = 0
          while (i < totalModelNum) {
            {
              try {
                val modelList: RList = models.asList.at(i).asList
                val univariateModel: UnivariateModel = new UnivariateModel
                univariateModel.setArimaP(modelList.at("p").asInteger)
                univariateModel.setArimaD(modelList.at("d").asInteger)
                univariateModel.setArimaQ(modelList.at("q").asInteger)
                univariateModel.setForecast(modelList.at("forecast").asDoubles)
                univariateModel.setResidualAutocorrelations((modelList.at("residualAutocorrelations").asString).toBoolean)
                univariateModel.setResidualHeteroscedastic((modelList.at("residualHeteroscedastic").asString).toBoolean)
                univariateModel.setNormality((modelList.at("normality").asString).toBoolean)
                univariateModel.setAic(modelList.at("aic").asDouble)
                univariateModel.setSbc(modelList.at("sbc").asDouble)
                univariateModel.setrSquare(modelList.at("rSquare").asDouble)
                univariateModel.setrSquareAdjusted(modelList.at("rSquareAdjusted").asDouble)
                univariateModel.setRmse(modelList.at("rmse").asDouble)
                univariateModel.setMae(modelList.at("mae").asDouble)
                univariateModel.setMape(modelList.at("mape").asDouble)
                univariateModel.setPackages(modelList.at("package").asString)
                univariateModel.setEmptyModel(false)
                univariateModels ::= univariateModel
              }
              catch {
                case e: NullPointerException => {
                  val univariateModel: UnivariateModel = new UnivariateModel
                  univariateModels ::= univariateModel
                }
              }
            }
            ({
              i += 1;
              i - 1
            })
          }
        }
      }
      else {
        val univariateModel: UnivariateModel = new UnivariateModel
        univariateModels ::= univariateModel
      }
    }
    catch {
      case e: RserveException => {
        e.printStackTrace
      }
      case e: REngineException => {
        e.printStackTrace
      }
      case e: REXPMismatchException => {
        e.printStackTrace
      }
    }

    rConnection.close()
    univariateModels
  }

  def createGarch(aramaParameter: ArmaParameter, ts: Array[Double]): UnivariateModel = {
    val rConnection = new RConnection("10.211.55.3", 6311)
    rConnection.eval("source(\"/dkShell/Rfile/univariateTimeSeriesModel.R\")")
    rConnection.eval("source(\"/dkShell/Rfile/loadpackage.R\")")

    val tsValue: Array[Double] = ts
    val arrP = Array(aramaParameter.getPacf)
    val arrD = Array(aramaParameter.getD)
    val arrQ = Array(aramaParameter.getAcf)
    val univariateModel: UnivariateModel = new UnivariateModel

    try {
      rConnection.assign("ts", tsValue)
      rConnection.assign("predictData", tsValue)
      rConnection.assign("p", arrP)
      rConnection.assign("d", arrD)
      rConnection.assign("q", arrQ)
      val models: REXP = rConnection.eval("createArimaGarchModel(ts, predictData, p, d, q)")
      if (models.length > 0) {
        try {
          val modelList: RList = models.asList.at(0).asList
          univariateModel.setArimaP(modelList.at("p").asInteger)
          univariateModel.setArimaD(modelList.at("d").asInteger)
          univariateModel.setArimaQ(modelList.at("q").asInteger)
          univariateModel.setGarchP(modelList.at("garchP").asInteger)
          univariateModel.setGarchQ(modelList.at("garchQ").asInteger)
          univariateModel.setForecast(modelList.at("forecast").asDoubles)
          univariateModel.setResidualAutocorrelations((modelList.at("residualAutocorrelations").asString).toBoolean)
          univariateModel.setResidualHeteroscedastic((modelList.at("residualHeteroscedastic").asString).toBoolean)
          univariateModel.setNormality((modelList.at("normality").asString).toBoolean)
          univariateModel.setAic(modelList.at("aic").asDouble)
          univariateModel.setSbc(modelList.at("sbc").asDouble)
          univariateModel.setrSquare(modelList.at("rSquare").asDouble)
          univariateModel.setrSquareAdjusted(modelList.at("rSquareAdjusted").asDouble)
          univariateModel.setRmse(modelList.at("rmse").asDouble)
          univariateModel.setMae(modelList.at("mae").asDouble)
          univariateModel.setMape(modelList.at("mape").asDouble)
          univariateModel.setPackages(modelList.at("package").asString)
          univariateModel.setEmptyModel(false)
        }
      }
    }
    catch {
      case e: RserveException => {
        e.printStackTrace
      }
      case e: REngineException => {
        e.printStackTrace
      }
      case e: REXPMismatchException => {
        e.printStackTrace
      }
    }
    rConnection.close()
    univariateModel
  }

}
