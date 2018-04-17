package ru.dgolubets.reactjs.server.actors

import scala.util.{Failure, Success, Try}

import akka.actor.{Actor, ActorLogging, Props}

import ru.dgolubets.reactjs.server.script.{ScriptContext, ScriptSource}

private[server] class RenderActor private[actors](sources: Seq[ScriptSource]) extends Actor with ActorLogging {

  import Messages._

  log.debug("Starting RenderActor..")

  private val scriptContext = ScriptContext()

  locally {
    scriptContext.exportSymbol("logger", log)

    log.debug("Evaluating polyfills..")
    scriptContext.eval(ScriptSource.fromResource("ru.dgolubets.reactjs.server/polyfills/base.js"))

    for (source <- sources) {
      log.debug(s"Evaluating $source..")
      scriptContext.eval(source)
    }
  }

  log.debug(s"Initialized.")

  override def receive: Receive = {
    case RenderRequest(functionName, state) =>
      log.debug(s"Render request ($functionName, $state)")
      val result = Try {
        scriptContext.eval(s"$functionName($state)").asString()
      } match {
        case Success(v) => Right(v)
        case Failure(e) => Left(e)
      }
      log.debug(s"Render result: $result")
      sender ! RenderResponse(result)
  }

  override def postStop(): Unit = {
    scriptContext.close()
    log.debug("Stopped RenderActor.")
  }
}

private[server] object RenderActor {

  def props(sources: Seq[ScriptSource]): Props = Props(new RenderActor(sources))
}