package com.example

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout

object ServiceActor {
  final case class ActionPerformed(description: String)
  final case class SendMessage(messageReq: String)
  final case object GetServices

  def props: Props = Props[ServiceActor]
}

class ServiceActor extends Actor with ActorLogging {
  import ServiceActor._
  import TwilioService.sendMessage
  import scala.concurrent.duration._
  import connectors.ElasticsearchConnector.getAllServices
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout: Timeout = 20 seconds

  def receive: Receive = {
    case SendMessage(messageReq) =>
      sender() ? sendMessage(messageReq)
    case GetServices =>
      sender() ? {
        getAllServices().map {
          case Some(x) => ActionPerformed(x.toString)
          case None => ActionPerformed("something went wrong")
        }
      }
  }
}
