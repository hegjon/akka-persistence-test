package com.vizrt.test.akka.persistence

import akka.actor.{Props, ActorRef, ActorLogging, Actor}

class Gatekeeper extends Actor with ActorLogging {
  private var activeTransfers = Map[Transfer, ActorRef]()

  override def receive = {
    case NewTransfer(transfer) =>
      log.info(s"New transfer: $transfer")
      val child = context.actorOf(Props(classOf[TransferActor], transfer), transfer.id)
      activeTransfers += (transfer -> child)
      child ! StartExport
  }
}
