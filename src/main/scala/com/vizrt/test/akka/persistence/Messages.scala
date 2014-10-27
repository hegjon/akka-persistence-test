package com.vizrt.test.akka.persistence

case class Transfer(id: String)

//commands
case class NewTransfer(transfer: Transfer)
case object StartExport
