package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, Actor}

class Gatekeeper extends Actor with ActorLogging {
  override def receive = {
    case NewTransfer(transfer) => log.info(s"New transfer: $transfer")
  }
}
