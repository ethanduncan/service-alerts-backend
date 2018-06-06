package com.example

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout

object ServiceActor {
  final case class ActionPerformed(description: String)
  final case class SendMessage(messageReq: String)
  final case object GetBadServices
  final case object SendBadServiceSMS

  def props: Props = Props[ServiceActor]
}

class ServiceActor extends Actor with ActorLogging {
  import ServiceActor._
  import connectors.TwilioService.sendMessage
  import scala.concurrent.duration._
  import connectors.ElasticsearchConnector.{ getBadServices, sendBadServiceSMS }
  import scala.concurrent.ExecutionContext.Implicits.global

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
      sender ? {
        sendBadServiceSMS().map(x => x)
      }
  }
}
