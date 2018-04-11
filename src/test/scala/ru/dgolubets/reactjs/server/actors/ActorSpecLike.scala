package ru.dgolubets.reactjs.server.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.concurrent.duration._

trait ActorSpecLike extends TestKitBase with ImplicitSender with BeforeAndAfterAll {
  this: Suite =>

  override implicit lazy val system: ActorSystem = ActorSystem()

  val timeout = 10 seconds

  protected override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }
}
