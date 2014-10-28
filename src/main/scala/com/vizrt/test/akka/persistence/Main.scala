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

    val transfer = Messages2.Transfer.newBuilder()
      .setId(UUID.randomUUID().toString)
      .setCreated(System.currentTimeMillis())
      .setMosXml(<mos><transferMedia>http://server.com/asset/{assetId}</transferMedia></mos>.toString)

    val newTransfer = Messages2.NewTransfer.newBuilder()
      .setTransfer(transfer)
      .build()

    gatekeeper ! newTransfer
  }
}
