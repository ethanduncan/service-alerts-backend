package com.example

import akka.actor.{ Actor, ActorLogging, Props }

object ServiceActor {
  final case class ActionPerformed(description: String)
  final case object SendMessage
  final case object GetUpdate

  def props: Props = Props[ServiceActor]
}

class ServiceActor extends Actor with ActorLogging {
  import ServiceActor._

  def receive: Receive = {
    case SendMessage =>
      sender() ! ActionPerformed("A message.")
    case GetUpdate =>
      sender() ! ActionPerformed("An update.")
  }
}
