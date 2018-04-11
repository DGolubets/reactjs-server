package ru.dgolubets.reactjs.server.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, AllForOneStrategy, Props}
import akka.routing.RoundRobinPool
import ru.dgolubets.reactjs.server.RenderServerSettings
import ru.dgolubets.reactjs.server.script.FileScriptSource

class RenderServerActor(settings: RenderServerSettings) extends Actor with ActorLogging {

  import Messages._

  private lazy val router = {
    val sources = settings.sources.map(f => new FileScriptSource(f))
    context.actorOf(RoundRobinPool(settings.nInstances).props(RenderActor.props(sources)), "router")
  }

  // All render actors should be initialized correctly or all should die
  override val supervisorStrategy = AllForOneStrategy(0) {
    case _: Throwable => Stop
  }

  override def receive: Receive = {
    case request: RenderRequest =>
      router forward request
  }
}

object RenderServerActor {
  def props(settings: RenderServerSettings): Props = Props(new RenderServerActor(settings))
}
