package utils

import com.example.ServiceActor.ActionPerformed
import models.MessageRequest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val messageRequestJsonFormat = jsonFormat1(MessageRequest)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
