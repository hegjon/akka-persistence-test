package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor

class Gatekeeper extends PersistentActor with ActorLogging {
  private var activeTransfers = Map[Messages2.Transfer, ActorRef]()

  override def persistenceId: String = "transfers"

  override def receiveRecover = {
    case t: Messages2.NewTransfer => newTransfer2(t)
  }

  override def receiveCommand = {
    case t: Messages2.NewTransfer => persist(t)(newTransfer2)
  }

  private def newTransfer2(t: Messages2.NewTransfer): Unit = {
    log.info(s"NewTransfer2: $t")
    val transfer = t.getTransfer
    val transferActor = context.actorOf(Props(classOf[TransferActor], transfer), transfer.getId)
    activeTransfers += (transfer -> transferActor)
  }
}
