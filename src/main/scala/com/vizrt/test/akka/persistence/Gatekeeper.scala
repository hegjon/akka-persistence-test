package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor

class Gatekeeper extends PersistentActor with ActorLogging {
  private var activeTransfers = Map[Transfer, ActorRef]()

  override def persistenceId: String = "transfers"

  override def receiveRecover = {
    case t: NewTransfer => newTransfer(t)
  }

  override def receiveCommand = {
    case t: NewTransfer => persist(t)(newTransfer)
  }

  private def newTransfer(t: NewTransfer): Unit = {
    log.info(t.toString)
    val transferActor = context.actorOf(Props(classOf[TransferActor], t.transfer), t.transfer.id)
    activeTransfers += (t.transfer -> transferActor)
  }
}
