package com.oring.smartcity.test.jobs

/**
  * Created by WeiChen on 2016/6/19.
  */

import com.oring.smartcity.dao.Data
import com.oring.smartcity.microservice.Job

class JobA extends Job {
  override def init(confMap: Map[String, String]): Unit = {
    println("Job A init !")

    //get properties in config.json
    println(confMap.mkString(","))

    //send data to B
    val req_data = new Data("{\"request_data\":\"Hello\"}")
    println("A send a request to B.")
    response("JobB", req_data)
  }

  override def receiveData(data: String): Unit = {
    println(data)
  }
}
