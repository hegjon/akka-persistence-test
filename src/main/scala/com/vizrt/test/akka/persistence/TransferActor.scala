package com.vizrt.test.akka.persistence

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

class TransferActor extends PersistentActor with ActorLogging {
  override def persistenceId = "transfer-x"

  override def receiveRecover = {
    case x => log.info(s"Recover: $x")
  }

  override def receiveCommand = {
    case x => log.info(s"Command: $x")
  }

}
