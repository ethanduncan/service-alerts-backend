package connectors

<<<<<<< HEAD
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.example.Routes
import com.typesafe.config.ConfigFactory
import models.{ ServiceResponse }
import utils.JsonSupport

import scala.concurrent.Future

object ElasticsearchConnector extends HttpConnector with JsonSupport {

  lazy val config = ConfigFactory.load("application.conf")
  lazy val log = Logging(system, classOf[Routes])

  lazy val uri = config.getString("elasticsearch.uri")

  def getAllServices(): Future[Option[ServiceResponse]] = {
    val servicesUri = s"${uri}tc/_search"

    httpGetRequest(servicesUri).flatMap {
      resp =>
        resp.status match {
          case OK =>
            Unmarshal(resp.entity).to[ServiceResponse].map {
              x => Some(x)
            }
          case status =>
            log.error(status.toString)
            Future.successful(None)
        }
    }
  }
}
