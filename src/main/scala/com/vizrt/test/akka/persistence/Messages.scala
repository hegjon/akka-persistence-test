package com.vizrt.test.akka.persistence

case class Transfer(id: String)

case class NewTransfer(transfer: Transfer)

case object StartExport
case object ExportSuccess

case object StartTranscode
case object TranscodeSuccess
