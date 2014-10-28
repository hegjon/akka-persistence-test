package com.vizrt.test.akka.persistence

import java.util.UUID

import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import scala.util.Random

object Main extends App {
  println("Starting")

  implicit val system = ActorSystem("persistence-test")
  val gatekeeper = system.actorOf(Props[Gatekeeper], "gatekeeper")

  import system.dispatcher
  system.scheduler.schedule(5 second, 65 seconds) {
    val assetId = Random.nextInt()
    val mosXml = <mos><transferMedia>http://server.com/asset/{assetId}</transferMedia></mos>.toString

    val created = System.currentTimeMillis()
    val transfer = new Transfer(id = UUID.randomUUID().toString, created, mosXml)

    gatekeeper ! NewTransfer(transfer)
  }
}
