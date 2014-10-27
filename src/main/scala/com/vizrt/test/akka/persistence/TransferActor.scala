package com.vizrt.test.akka.persistence

import akka.actor.ActorLogging
import akka.persistence.{RecoveryCompleted, PersistentActor}

import scala.concurrent.duration._

class TransferActor(transfer: Transfer) extends PersistentActor with ActorLogging {
  override def persistenceId = s"transfer-${transfer.id}"

  private var lastRecover: Option[String] = None

  override def receiveRecover = {
    case RecoveryCompleted =>
      log.info(s"Recovery completed, last state=$lastRecover")
    case x =>
      log.info(s"Recover: $x")
      lastRecover = Some(x.toString)
  }

  def noop(event: AnyRef): Unit = ()

  override def receiveCommand = exporting

  import context.dispatcher

  def exporting: Receive = {
    case m@StartExport =>
      log.info(m.toString)
      persist(m)(noop)
      context.system.scheduler.scheduleOnce(5 seconds, context.self, ExportSuccess)
    case m@ExportSuccess =>
      log.info(m.toString)
      persist(m)(noop)
      context.self ! StartTranscode
      context.become(transcoding)
    case x => log.info(s"Unknown command: $x")
  }

  def transcoding: Receive = {
    case m@StartTranscode =>
      log.info(m.toString)
      persist(m)(noop)
      context.system.scheduler.scheduleOnce(15 seconds, context.self, TranscodeSuccess)
    case m@TranscodeSuccess =>
      log.info(m.toString)
      persist(m)(noop)
      context.self ! StartTranscode
  }
}
