package connectors

import akka.http.scaladsl.model.StatusCodes._
import com.typesafe.config.ConfigFactory

object ElasticsearchConnector extends HttpConnector {

  lazy val config = ConfigFactory.load("application.conf")

  lazy val uri = config.getString("elasticsearch.uri")

  def getAllServices() = {
    val servicesUri = s"${uri}tc/_search"

    httpGetRequest(servicesUri).map {
      resp =>
        resp.status match {
          case OK => resp.entity.toString
          case status => status.toString()
        }
    }
  }
}
