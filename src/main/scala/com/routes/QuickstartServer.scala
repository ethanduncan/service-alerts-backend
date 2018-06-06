package com.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.{ Routes, ServiceActor }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuickstartServer extends App with Routes {

  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val serviceActor: ActorRef = system.actorOf(ServiceActor.props, "serviceActor")

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)

}

