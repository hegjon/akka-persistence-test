package com.vizrt.test.akka.persistence

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.vizrt.test.akka.persistence.Messages2._

import scala.concurrent.duration._

object TransferActor {
  def props(transfer: Transfer) = Props(classOf[TransferActor], transfer)
}

class TransferActor(transfer: Transfer) extends PersistentActor with ActorLogging {
  override def persistenceId = s"transfer-${transfer.getId}"

  private var recoveredState: Option[Any] = None

  import context.dispatcher

  override def receiveRecover = {
    case RecoveryCompleted => recoveredState match {
      case None => init
      case Some(s: StartExport) => startExport(s)
      case Some(s: ExportSuccess) => exportSuccess(s)
      case Some(s: StartTranscode) => startTranscode(s)
      case Some(s: TranscodeSuccess) => transcodeSuccess(s)
      case Some(other) => log.info(s"Recovery completed, unknown state=$other")
    }

    case x => recoveredState = Some(x)
  }

  override def receiveCommand = exporting

  def exporting: Receive = {
    case m: StartExport => persist(m)(startExport)
    case m: ExportSuccess => persist(m)(exportSuccess)
    case x => log.info(s"Unknown command: $x")
  }

  private def init: Unit = self ! StartExport.getDefaultInstance

  private def startExport(m: StartExport): Unit = {
    log.info(m.toString)
    val exportActor = context.actorOf(ExportActor.props(transfer), "export")
    exportActor ! m
    context.become(exporting)
  }

  private def exportSuccess(m: ExportSuccess): Unit = {
    log.info(m.toString)
    self ! StartTranscode.getDefaultInstance
    context.become(transcoding)
  }

  def transcoding: Receive = {
    case m: StartTranscode => persist(m)(startTranscode)
    case m: TranscodeSuccess => persist(m)(transcodeSuccess)
    case x => log.info(s"Unknown command: $x")
  }

  private def startTranscode(m: StartTranscode): Unit = {
    log.info(m.toString)
    context.system.scheduler.scheduleOnce(15 seconds, self, TranscodeSuccess.getDefaultInstance)
  }

  private def transcodeSuccess(m: TranscodeSuccess): Unit = {
    log.info(m.toString)
    self ! StartPublishing.getDefaultInstance
    context.become(publishing)
  }

  def publishing: Receive = {
    case m: StartPublishing => persist(m)(startPublish)
    case m: PublishingSuccess => persist(m)(publishSuccess)
    case x => log.info(s"Unknown command: $x")
  }

  def startPublish(m: StartPublishing): Unit = {
    log.info(m.toString)
    context.system.scheduler.scheduleOnce(10 seconds, self, PublishingSuccess.getDefaultInstance)
  }

  def publishSuccess(m: PublishingSuccess): Unit = {
    log.info(m.toString)
    context.stop(self)
  }
}
