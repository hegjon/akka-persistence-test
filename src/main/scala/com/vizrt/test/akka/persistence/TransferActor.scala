package com.vizrt.test.akka.persistence

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

class TransferActor(transfer: Transfer) extends PersistentActor with ActorLogging {
  override def persistenceId = s"transfer-${transfer.id}"

  override def receiveRecover = {
    case x => log.info(s"Recover: $x")
  }

  override def receiveCommand = {
    case x => log.info(s"Command: $x")
  }

}
