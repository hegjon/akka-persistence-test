package com.vizrt.test.akka.persistence

import akka.actor.{Props, Actor}
import com.vizrt.test.akka.persistence.Messages._

object ExportActor {
  def props(transfer: Transfer) = Props(classOf[ExportActor], transfer)
}

class ExportActor(transfer: Transfer) extends Actor {
  override def receive = {
    case m: StartExport =>
      Thread.sleep(5000) // XXX blocks
      sender ! ExportSuccess.getDefaultInstance
  }
}
