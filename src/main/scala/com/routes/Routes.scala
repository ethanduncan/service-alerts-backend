package com.example

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import com.example.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout

trait Routes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  def serviceActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val routes: Route =
    pathPrefix("message") {
      concat(
        pathEnd {
          concat(
            post {
              entity(as[Message]) { message =>
                val messageSent: Future[ActionPerformed] =
                  (serviceActor ? SendMessage).mapTo[ActionPerformed]
                onSuccess(messageSent) { performed =>
                  log.info("Message Sent")
                  complete((StatusCodes.Ok, performed))
                }
              }
            })
        })
    }
}