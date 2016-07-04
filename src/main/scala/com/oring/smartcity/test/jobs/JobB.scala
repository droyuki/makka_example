package com.oring.smartcity.test.jobs

import com.oring.smartcity.dao.Data
import com.oring.smartcity.microservice.Job

/**
  * Created by WeiChen on 2016/6/19.
  */
class JobB extends Job{
  override def init(confMap:Map[String,String]): Unit = {
    println("Job B init !")
  }

  override def receiveData(d: String): Unit = {
    println("Job B receive data: " + d)
    //send response to Job A
    val res_data = new Data("{\"response_data\":\"I got it!\"}")
    response("JobA", res_data)
  }

}
