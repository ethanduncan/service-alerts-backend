package utils

import com.example.ServiceActor.ActionPerformed
import models._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{ DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat }

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val messageRequestJsonFormat = jsonFormat1(MessageRequest)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val serviceModelJsonFormat = jsonFormat2(ServiceJson)

  implicit object ServiceModelFormat extends DefaultJsonProtocol with RootJsonFormat[ServiceModel] {
    override def write(obj: ServiceModel): JsValue = jsonFormat1(ServiceModel).write(obj)

    override def read(json: JsValue): ServiceModel = {
      val jsonMap = json.asJsObject.fields
      ServiceModel(
        jsonMap.get("_source").get.convertTo[ServiceJson])
    }
  }

  implicit object ServiceResponseFormat extends DefaultJsonProtocol with RootJsonFormat[ServiceResponse] {
    override def write(obj: ServiceResponse): JsValue = jsonFormat1(ServiceResponse).write(obj)

    override def read(json: JsValue): ServiceResponse = {
      val jsonMap = json.asJsObject.fields
      val hits =
        jsonMap.get("hits").map(x => x.convertTo[JsObject].getFields("hits")).get.toSeq
      val y: Seq[ServiceModel] = hits.map(x => x.convertTo[Seq[ServiceModel]]).flatten

      ServiceResponse(y)
    }
  }
}

