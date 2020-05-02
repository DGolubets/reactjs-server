package ru.dgolubets.reactjs.server.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, Stash, Terminated}
import akka.routing.RoundRobinPool
import ru.dgolubets.reactjs.server.RenderServerSettings
import ru.dgolubets.reactjs.server.script.FileScriptSource

private[server] class RenderServerActor(settings: RenderServerSettings) extends Actor with Stash with ActorLogging {

  import Messages._

  private var router: Option[ActorRef] = None

  private val monitor: Option[ActorRef] = settings.watch.map { watchSettings =>
    val files = settings.sources.collect {
      case FileScriptSource(file, _) => file
    }
    context.actorOf(SourcesMonitorActor.props(self, watchSettings.root, files, watchSettings.delay))
  }

  override val supervisorStrategy = OneForOneStrategy(0) {
    case _: Throwable =>
      if (monitor.contains(sender)) {
        // if monitor fails for some reason - restart it
        Restart
      }
      else {
        // router, however, depends
        if (monitor.nonEmpty) {
          // if we are watching - just stop and wait for source changes to restart
          Stop
        }
        else {
          // if not - we can't do anything but to escalate to the "boss"
          Escalate
        }
      }
  }

  def restartRouter(): Unit = {
    for (r <- router) {
      // stop existing router
      context.unwatch(r)
      context.stop(r)
    }

    val newRouter = context.actorOf(
      RoundRobinPool(settings.nInstances)
        .props(RenderActor
          .props(settings.sources)
        ))
    context.watch(newRouter)
    router = Some(newRouter)
  }

  private def readyHandler: Receive = {
    case request: RenderRequest =>
      for (r <- router) {
        r forward request
      }
  }

  private def waitingHandler: Receive = {
    case request: RenderRequest =>
      log.warning(s"Stashing request..")
      stash()
  }

  private def becomeReady(): Unit = {
    restartRouter()
    unstashAll()
    context.become(ready)
  }

  private def becomeWaiting(): Unit = {
    context.become(waiting)
  }

  private def commonHandler: Receive = {
    case SourcesChanged(files) =>
      log.info(s"Restarting due to changed sources: $files")
      becomeReady()
    case SourcesMissing(files) =>
      log.warning(s"Waiting for missing sources: $files")
      context.become(waiting)
    case Terminated(a) =>
      if (router.contains(a)) {
        log.debug(s"Router terminated")
        context.become(waiting)
      }
  }

  private val ready = readyHandler orElse commonHandler
  private val waiting = waitingHandler orElse commonHandler

  if (monitor.nonEmpty) {
    becomeWaiting()
  }
  else {
    becomeReady()
  }

  override def receive: Receive = commonHandler
}

private[server] object RenderServerActor {
  def props(settings: RenderServerSettings): Props = Props(new RenderServerActor(settings))
}
