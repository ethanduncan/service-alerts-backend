package connectors

import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.example.Routes
import com.example.ServiceActor.ActionPerformed
import com.typesafe.config.ConfigFactory
import models.{ AssystTicketModel, ElasticsearchResponse, ServiceModel }
import utils.JsonSupport
import TwilioService.sendMessage

import scala.concurrent.Future

object ElasticsearchConnector extends HttpConnector with JsonSupport {

  lazy val config = ConfigFactory.load("application.conf")
  lazy val log = Logging(system, classOf[Routes])

  lazy val uri = config.getString("elasticsearch.uri")

  private def getAllServices(): Future[Option[ElasticsearchResponse]] = {
    val servicesUri = s"${uri}tc/_search"

    httpGetRequest(servicesUri).flatMap {
      resp =>
        resp.status match {
          case OK =>
            Unmarshal(resp.entity).to[ElasticsearchResponse].map {
              x => Some(x)
            }
          case status =>
            log.error(status.toString)
            Future.successful(None)
        }
    }
  }

  def getBadServices(): Future[Option[ActionPerformed]] = {
    getAllServices().map {
      case Some(elasticsearchResp) => {
        val serviceNames = for (
          json <- elasticsearchResp.hits;
          if json._source.convertTo[ServiceModel].status == "bad"
        ) yield (json._source.convertTo[ServiceModel].name)
        Some(ActionPerformed("Bad services: " + serviceNames.mkString(" ")))
      }
      case None => None
    }
  }

  def sendBadServiceSMS(): Future[ActionPerformed] = {
    getBadServices().map {
      case Some(actionPerformed) => {
        sendMessage(actionPerformed.description)
        actionPerformed
      }
      case None => ActionPerformed("All services okay")
    }
  }

  def getTickets(): Future[Option[Seq[AssystTicketModel]]] = {
    val assystUri = s"${uri}tc-assyst/ticket/_search"

    httpGetRequest(assystUri).flatMap {
      resp =>
        resp.status match {
          case OK =>
            Unmarshal(resp.entity).to[ElasticsearchResponse].map {
              x =>
                val y = for (
                  json <- x.hits;
                  ticket = json._source.convertTo[AssystTicketModel]
                  if ticket.priority == 1 || ticket.priority == 2
                ) yield (ticket)
                Some(y)
            }
          case status =>
            log.error(status.toString)
            Future.successful(None)
        }
    }
  }
}
