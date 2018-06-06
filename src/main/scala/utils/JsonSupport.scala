package utils

import com.example.ServiceActor.ActionPerformed
import models._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{ DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat }

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val messageRequestJsonFormat = jsonFormat1(MessageRequest)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val serviceModelJsonFormat = jsonFormat2(ServiceModel)

  //  implicit val assystTicketModelJsonFormat = jsonFormat2(AssystTicketModel)
  implicit val assystTicketModelJsonFormat = jsonFormat(
    AssystTicketModel.apply,
    "Priority", "Status", "SVD Assigned Name", "Date/Time Logged")

  implicit object ElasticsearchJsonModelFormat extends DefaultJsonProtocol with RootJsonFormat[ElasticsearchJsonModel] {
    override def write(obj: ElasticsearchJsonModel): JsValue = jsonFormat1(ElasticsearchJsonModel).write(obj)

    override def read(json: JsValue): ElasticsearchJsonModel = {
      val jsonMap = json.asJsObject.fields
      ElasticsearchJsonModel(
        jsonMap.get("_source").get.convertTo[JsValue])
    }
  }

  implicit object ElasticsearchResponseFormat extends DefaultJsonProtocol with RootJsonFormat[ElasticsearchResponse] {
    override def write(obj: ElasticsearchResponse): JsValue = jsonFormat1(ElasticsearchResponse).write(obj)

    override def read(json: JsValue): ElasticsearchResponse = {
      val jsonMap = json.asJsObject.fields
      val hits =
        jsonMap.get("hits").map(x => x.convertTo[JsObject].getFields("hits")).get
      val jsonModelList: Seq[ElasticsearchJsonModel] =
        hits.flatMap(x => x.convertTo[Seq[ElasticsearchJsonModel]])
      ElasticsearchResponse(jsonModelList)
    }
  }
}

