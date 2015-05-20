package ru.dgolubets.reactjs.server.api.actors

import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender, TestKitBase}
import org.scalatest.{Suite, BeforeAndAfterAll}

/**
 * Created by Dima on 18.05.2015.
 */
trait ActorSpecLike extends TestKitBase with ImplicitSender with BeforeAndAfterAll  {
  this: Suite =>

  override implicit lazy val system: ActorSystem = ActorSystem()

  protected override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }
}
