package timeSeriesPredict

import java.util

import _root_.other.UnivariateModel
import _root_.other.UnivariateModel
import _root_.other.UnivariateModel
import org.rosuda.REngine.{RList, REXP, REXPMismatchException, REngineException}
import org.rosuda.REngine.Rserve.{RserveException, RConnection}


/**
 * Created by WanEnFu on 16/7/27.
 */
class Tsp {

  def unitRootTest(ts: Array[Double]): Int = {
    val rConnections = new RConnection()
    var d: Int = 0
    try {
      rConnections.assign("ts", ts)
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
    val rConnections = new RConnection()
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
    var armaParameters = List.empty
    for(i <- 1 to 1+acfNum ) {
      for(j <- acfNum+1 to acfPacf.length) {
        val armaParameter: ArmaParameter = new ArmaParameter(acfPacf(i), d, acfPacf(j))
        armaParameters::=armaParameter
      }
    }

    rConnections.close()
    return armaParameters
  }

  def createModel(pacfAcf: List[ArmaParameter], ts: Array[Double]): Unit = {
    val models = pacfAcf.par.map(e => {
      val arimaModel = createArima(e, ts)
      val garchModel = createGarch(e, ts)
    })
  }

  def createArima(aramaParameter: ArmaParameter,  ts: Array[Double]): Unit = {
    val rConnection = new RConnection()
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
      val models: REXP = rConnection.eval("createArimaModel(ts, predictData, p, d, q)")
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
                univariateModels::=univariateModel
              }
              catch {
                case e: NullPointerException => {
                  val univariateModel: UnivariateModel = new UnivariateModel
                  univariateModels ::= univariateModel
                }
              }
            }
            ({
              i += 1; i - 1
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
  }

  def createGarch(aramaParameter: ArmaParameter,  ts: Array[Double]): Unit ={
    val rConnection = new RConnection()
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
        catch {
          case e: NullPointerException => {
            val univariateModel: UnivariateModel = new UnivariateModel
            univariateModels ::= univariateModel
          }
        }
      }
      else {
        univariateModel.setRealTimeStamp(realTimeStamp)
        univariateModel.setTimeStamp(lastTimeStamp)
        univariateModel.setBoltId(boltId)
        univariateModel.setTaskNum(taskNum)
        univariateModel.setWindowSize(windowSize)
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
  }

}
