package com.vizrt.test.akka.persistence

import akka.actor.{Props, Actor}
object ExportActor {
  def props(transfer: Transfer) = Props(classOf[ExportActor], transfer)
}

class ExportActor(transfer: Transfer) extends Actor {
  override def receive: Receive = {
    case StartExport =>
      Thread.sleep(5000) // XXX blocks
      sender ! ExportSuccess
  }
}
