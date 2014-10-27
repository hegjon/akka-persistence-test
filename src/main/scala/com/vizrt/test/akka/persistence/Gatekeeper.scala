package com.vizrt.test.akka.persistence

import akka.actor.Actor

class Gatekeeper extends Actor {
  override def receive = {
    case "new-transfer" => println("new transfer!!")
  }
}
