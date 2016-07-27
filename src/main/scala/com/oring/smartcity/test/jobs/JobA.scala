package com.oring.smartcity.test.jobs

import com.oring.smartcity.makka.{Data, Job}

/**
  * Created by WeiChen on 2016/6/19.
  */


class JobA extends Job {
  override def init(): Unit = {
    println("Job A init !")

    //send data to B
    val req_data = new Data("{\"request_data\":\"Hello\"}")
    println("A send a request to B.")
    pipe("JobB", req_data)
  }

  override def receiveData(data: String): Unit = {
    println(data)
  }
}
