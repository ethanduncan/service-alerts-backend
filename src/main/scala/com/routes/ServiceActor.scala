package com.example

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout
import utils.CSVFileWriter._
import utils.JsonSupport

object ServiceActor {
  final case class ActionPerformed(description: String)
  final case class SendMessage(messageReq: String)
  final case object GetPriorityTickets
  final case object GetBadServices
  final case object SendBadServiceSMS

  def props: Props = Props[ServiceActor]
}

class ServiceActor extends Actor with ActorLogging with JsonSupport {
  import ServiceActor._
  import connectors.ElasticsearchConnector.{ getBadServices, getTickets, sendBadServiceSMS }
  import connectors.TwilioService.sendMessage

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val timeout: Timeout = 20 seconds

  def receive: Receive = {
    case SendMessage(messageReq) =>
      sender() ? sendMessage(messageReq)
    case GetBadServices =>
      sender() ? {
        getBadServices().map {
          case Some(serviceNames) => serviceNames
          case None => ActionPerformed("All services okay")
        }
      }
    case SendBadServiceSMS =>
      sender() ? {
        sendBadServiceSMS().map(x => x)
      }
    case GetPriorityTickets =>
      sender() ? {
        getTickets().map {
          case Some(x) => {
            ActionPerformed(readCSVFile(x).mkString(" "))
          }
          case None => ActionPerformed("No new priority tickets")
        }
      }
  }
}
