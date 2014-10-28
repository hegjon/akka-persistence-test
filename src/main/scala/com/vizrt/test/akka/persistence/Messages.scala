package com.vizrt.test.akka.persistence

import java.net.URI

//mosXml is a gigant XML that is the input we get from end user / external system. Simplified in this test project
case class Transfer(id: String, created: Long, mosXml: String)

case class NewTransfer(transfer: Transfer)

case class StartExport(transferMedia: URI)
case object ExportSuccess

case object StartTranscode
case object TranscodeSuccess

case object StartPublish
case object PublishSuccess
