package com.vizrt.test.akka.persistence

import akka.actor.{Props, Actor}

object ExportActor {
  def props(transfer: Messages2.Transfer) = Props(classOf[ExportActor], transfer)
}

class ExportActor(transfer: Messages2.Transfer) extends Actor {
  override def receive: Receive = {
    case StartExport =>
      Thread.sleep(5000) // XXX blocks
      sender ! ExportSuccess
  }
}
