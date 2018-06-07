package connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

trait HttpConnector {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def singleRequest(request: HttpRequest): Future[HttpResponse] = Http().singleRequest(request)

  def httpGetRequest(uri: String): Future[HttpResponse] = {
    singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = uri,
        entity = HttpEntity(ContentTypes.`application/json`, """{
                                                               "size" : 250
                              }""")))
  }

}
