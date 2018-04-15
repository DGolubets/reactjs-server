package ru.dgolubets.reactjs.server.actors

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestKit, TestKitBase, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.concurrent.duration._

trait ActorSpecLike extends TestKitBase with ImplicitSender with BeforeAndAfterAll {
  this: Suite =>

  override implicit lazy val system: ActorSystem = ActorSystem()

  val timeout = 10 seconds

  protected override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

  def disposableActor[T](props: Props)(code: ActorRef => T) = {
    val probe = TestProbe()
    val actorRef = system.actorOf(props)
    probe watch actorRef
    try {
      code(actorRef)
    }
    finally {
      actorRef ! PoisonPill
      probe.expectTerminated(actorRef)
    }
  }
}
