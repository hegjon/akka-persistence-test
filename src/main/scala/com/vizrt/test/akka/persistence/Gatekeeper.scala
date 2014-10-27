package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.{RecoveryCompleted, PersistentActor}

class Gatekeeper extends PersistentActor with ActorLogging {
  private var activeTransfers = Map[Transfer, ActorRef]()

  override def persistenceId: String = "transfers"

  override def receiveRecover = {
    case RecoveryCompleted => log.info("Recovery completed!")
    case m@NewTransfer(transfer) =>
      log.info(s"Recover transfer: $transfer")
      val child = context.actorOf(Props(classOf[TransferActor], transfer), transfer.id)
      activeTransfers += (transfer -> child)
    case x => log.info(s"Recover: $x")
  }

  override def receiveCommand = {
    case m@NewTransfer(transfer) => persist(m) { m =>
      log.info(s"New transfer: $transfer")
      val child = context.actorOf(Props(classOf[TransferActor], transfer), transfer.id)
      activeTransfers += (transfer -> child)
    }
  }
}
