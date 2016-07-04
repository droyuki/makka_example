package com.oring.smartcity.test

import com.oring.smartcity.microservice.MSController

/**
  * Created by WeiChen on 2016/6/19.
  */

object Test {
  def main(args: Array[String]) {
    val msc = new MSController(args(0))
    msc.run()
  }
}
