package com.example

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout

object ServiceActor {
  final case class ActionPerformed(description: String)
  final case class SendMessage(messageReq: String)
  final case object GetUpdate

  def props: Props = Props[ServiceActor]
}

class ServiceActor extends Actor with ActorLogging {
  import ServiceActor._
  import TwilioService.sendMessage
  import scala.concurrent.duration._

  implicit val timeout: Timeout = 20 seconds

  def receive: Receive = {
    case SendMessage(messageReq) =>
      sender() ? sendMessage(messageReq)
    case GetUpdate =>
      sender() ! ActionPerformed("An update.")
  }
}
