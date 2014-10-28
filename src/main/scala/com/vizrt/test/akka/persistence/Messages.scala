package com.vizrt.test.akka.persistence

import java.net.URI

case class StartExport(transferMedia: URI)
case object ExportSuccess

case object StartTranscode
case object TranscodeSuccess

case object StartPublish
case object PublishSuccess
