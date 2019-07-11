package com.modulo3.flume.interceptors

import scala.collection.JavaConversions.collectionAsScalaIterable
//import com.modulo3_grupal.common.functions.GlobalFunctions._
//import com.fhuertas.icemd.bigdata2019.dsl.JsonUtils._


  class JsonCleanerInterceptor(var ctx: org.apache.flume.Context) extends org.apache.flume.interceptor.Interceptor {

  override def initialize() = {
    // No-op
  }

  override def intercept(event: org.apache.flume.Event): org.apache.flume.Event = {

    val input = new String(event getBody)

    //Se limpian las barras y las comillas:
    val output = input
        .replace("""\u005C""", "") //reemplazo los "\" por nada
        .drop(1) //borro la primera comilla
        .dropRight(1) //borro la última comilla


    event setBody output.getBytes
    event
  }

  override def intercept(events: java.util.List[org.apache.flume.Event]): java.util.List[org.apache.flume.Event] = {
    events foreach intercept
    events
  }

  override def close() = {
    // No-op
  }
}

object JsonCleanerInterceptor{

  class Builder extends org.apache.flume.interceptor.Interceptor.Builder {
    var ctx: org.apache.flume.Context = _

    override def configure(context: org.apache.flume.Context) = {
      this ctx = context
    }

    override def build(): org.apache.flume.interceptor.Interceptor = {
      new JsonCleanerInterceptor(ctx)
    }
  }
}