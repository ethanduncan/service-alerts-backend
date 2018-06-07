package com.example

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.example.ServiceActor._
import models.MessageRequest
import spray.json.JsValue
import utils.JsonSupport

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

trait Routes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  def serviceActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val routes: Route =
    pathPrefix("message") {
      concat(
        pathEnd {
          concat(
            post {
              entity(as[MessageRequest]) { message =>
                val messageSent: Future[ActionPerformed] =
                  (serviceActor ? SendMessage(message.message)).mapTo[ActionPerformed]
                onSuccess(messageSent) { performed =>
                  log.info("Message Sent")
                  complete((StatusCodes.OK, performed))
                }
              }
            })
        })
    } ~ pathPrefix("bad-services") {
      concat(
        pathEnd {
          concat(
            get {
              onComplete(serviceActor ? GetBadServices) {
                case Success(resp: Future[Seq[JsValue]]) => complete(StatusCodes.OK, resp)
                case Success(resp) => complete(StatusCodes.BadRequest, "dont know")
                case Failure(e) => complete(StatusCodes.InternalServerError)
              }
            })
        })
    } ~ pathPrefix("bad-services-sms") {
      concat(
        pathEnd {
          concat(
            get {
              onComplete(serviceActor ? SendBadServiceSMS) {
                case Success(resp: Future[Seq[JsValue]]) => complete(StatusCodes.OK, resp)
                case Success(resp) => complete(StatusCodes.BadRequest, "dont know")
                case Failure(e) => complete(StatusCodes.InternalServerError)
              }
            })
        })
    } ~ pathPrefix("tickets") {
      concat(
        pathEnd {
          concat(
            get {
              onComplete(serviceActor ? GetPriorityTickets) {
                case Success(resp: Future[Seq[JsValue]]) => complete(StatusCodes.OK, resp)
                case Success(resp) => complete(StatusCodes.BadRequest, "dont know")
                case Failure(e) => complete(StatusCodes.InternalServerError)
              }
            })
        })
    }
}