package util

import java.io.{File, FileInputStream}

import com.oring.smartcity.makka.{Data, Job}
import com.oring.smartcity.util.SslUtil
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import play.api.libs.json.Json

/**
  * Created by WeiChen on 2016/7/27.
  */
class MqttJob extends Job {
  var mqttClient: MqttClient = _

  override def init(): Unit = {
    try {
      val in = new FileInputStream(new File("src/main/resources/mqttConf.json"))
      val rawJson = Json.parse(in)
      val topics = (rawJson \ "topics").as[List[String]]
      val mqttBroker = (rawJson \ "broker").as[String]
      println(mqttBroker)
      println(topics)
      val options = new MqttConnectOptions
      val sslContext = new SslUtil().getSslContext("mytest", "src/main/resources/cacerts")
      options.setSocketFactory(sslContext.getSocketFactory)
      options.setUserName("test")
      options.setPassword("test".toCharArray())

      mqttClient = new MqttClient(mqttBroker, MqttClient.generateClientId(), new MemoryPersistence)
      mqttClient.connect(options)
      mqttClient.subscribe(topics.toArray)
      val callback = new MqttCallback {
        override def messageArrived(topic: String, message: MqttMessage): Unit = {
          topic match {
            case "/fund/portfolio/req" => pipe("PortfolioJob", new Data(message.toString))
            case "/fund/tsp/req" => {
              log.info(message.toString)
              pipe("Tspjob", new Data(message.toString))
            }
            case "/fund/4433/req" => pipe("FfttJob", new Data(message.toString))
            case "/fund/saveportfolio/req" => pipe("SaveJob", new Data(message.toString))
          }
        }

        override def connectionLost(cause: Throwable): Unit = {
          log.error(cause.toString)
        }

        override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
        }
      }
      mqttClient.setCallback(callback)
    } catch {
      case e => log.error(e.toString)
    }
  }

  override def receiveData(data: String): Unit = {
    val raw = Json.parse(data)
    val topic = (raw \ "topic").as[String]
    val response = Json.stringify((raw \ "data").get)
    mqttClient.publish(topic, new MqttMessage(response.getBytes("utf-8")))
  }
}
