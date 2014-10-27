package com.vizrt.test.akka.persistence

case class Transfer(id: String)
case class NewTransfer(transfer: Transfer)
