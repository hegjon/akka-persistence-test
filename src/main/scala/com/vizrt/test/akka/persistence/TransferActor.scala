package com.vizrt.test.akka.persistence

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, RecoveryCompleted}

import scala.concurrent.duration._

class TransferActor(transfer: Transfer) extends PersistentActor with ActorLogging {
  override def persistenceId = s"transfer-${transfer.id}"

  private var recoveredState: Option[Any] = None

  import context.dispatcher

  override def receiveRecover = {
    case RecoveryCompleted => recoveredState match {
      case None => init
      case Some(StartExport) => startExport(StartExport)
      case Some(ExportSuccess) => exportSuccess(ExportSuccess)
      case Some(StartTranscode) => startTranscode(StartTranscode)
      case Some(TranscodeSuccess) => transcodeSuccess(TranscodeSuccess)
      case Some(other) => log.info(s"Recovery completed, unknown state=$other")
    }

    case x => recoveredState = Some(x)
  }

  override def receiveCommand = exporting

  def exporting: Receive = {
    case m@StartExport => persist(m)(startExport)
    case m@ExportSuccess => persist(m)(exportSuccess)
    case x => log.info(s"Unknown command: $x")
  }

  private def init: Unit = self ! StartExport

  private def startExport(m: StartExport.type): Unit = {
    log.info(m.toString)
    context.actorOf(ExportActor.props(transfer), "export")
    context.become(exporting)
  }

  private def exportSuccess(m: ExportSuccess.type): Unit = {
    log.info(m.toString)
    self ! StartTranscode
    context.become(transcoding)
  }

  def transcoding: Receive = {
    case m@StartTranscode => persist(m)(startTranscode)
    case m@TranscodeSuccess => persist(m)(transcodeSuccess)
    case x => log.info(s"Unknown command: $x")
  }

  private def startTranscode(m: StartTranscode.type): Unit = {
    log.info(m.toString)
    context.system.scheduler.scheduleOnce(15 seconds, self, TranscodeSuccess)
  }

  private def transcodeSuccess(m: TranscodeSuccess.type): Unit = {
    log.info(m.toString)
    self ! StartPublish
    context.become(publishing)
  }

  def publishing: Receive = {
    case m@StartPublish =>
      log.info(m.toString)
      persist(m)(noop)
      context.system.scheduler.scheduleOnce(10 seconds, self, PublishSuccess)
    case m@PublishSuccess =>
      log.info(m.toString)
      persist(m)(noop)
      context.stop(self)
    case x => log.info(s"Unknown command: $x")
  }
}
