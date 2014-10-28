package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor
import com.vizrt.test.akka.persistence.Messages2._

class Gatekeeper extends PersistentActor with ActorLogging {
  private var activeTransfers = Map[Transfer, ActorRef]()

  override def persistenceId: String = "transfers"

  override def receiveRecover = {
    case t: NewTransfer => newTransfer2(t)
  }

  override def receiveCommand = {
    case t: NewTransfer => persist(t)(newTransfer2)
  }

  private def newTransfer2(t: NewTransfer): Unit = {
    log.info(t.toString)
    val transfer = t.getTransfer
    val transferActor = context.actorOf(Props(classOf[TransferActor], transfer), transfer.getId)
    activeTransfers += (transfer -> transferActor)
  }
}
