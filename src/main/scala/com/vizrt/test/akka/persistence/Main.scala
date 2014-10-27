package com.vizrt.test.akka.persistence

import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._

object Main extends App {
  println("Test")

  implicit val system = ActorSystem("persistence-test")
  val gatekeeper = system.actorOf(Props[Gatekeeper], "gatekeeper")

  import system.dispatcher
  system.scheduler.schedule(1 second, 20 seconds, gatekeeper, "new-transfer")
}
