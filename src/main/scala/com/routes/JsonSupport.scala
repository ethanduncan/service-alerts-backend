package com.example

import com.example.ServiceActor.ActionPerformed
import com.example.Message

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val messageJsonFormat = jsonFormat1(Message)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
