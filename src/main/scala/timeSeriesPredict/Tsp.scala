package timeSeriesPredict

import org.rosuda.REngine.{REXPMismatchException, REngineException}
import org.rosuda.REngine.Rserve.{RserveException, RConnection}

/**
 * Created by WanEnFu on 16/7/27.
 */
class Tsp {

  def unitRootTest(rConnections: RConnection, ts: Array[Double]): Int = {
    var d: Int = 0
    try {
      rConnections.assign("ts", ts)
      rConnections.assign("ts", ts)
      d = rConnections.eval("unitRootTestADF(ts)").asInteger
    }
    catch {
      case e: REngineException => {
        e.printStackTrace
      }
      case e: REXPMismatchException => {
        e.printStackTrace
      }
    }
    return d
  }

  def estimateAcfPacf(rConnections: RConnection, ts: Array[Double], d: Int): Array[Int] = {
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
    return acfPacf
  }


}
