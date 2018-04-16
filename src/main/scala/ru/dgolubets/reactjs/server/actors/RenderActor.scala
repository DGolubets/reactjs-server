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

    scriptContext.eval(ScriptSource.fromResource("ru.dgolubets.reactjs.server/polyfills/base.js"))

    for (source <- sources) {
      scriptContext.eval(source)
    }
  }

  override def receive: Receive = {
    case RenderRequest(functionName, state) =>
      log.debug(s"Render request ($functionName, $state)")
      val result = Try {
        scriptContext.eval(s"$functionName($state)").asString()
      } match {
        case Success(v) => Right(v)
        case Failure(e) => Left(e)
      }

      sender ! RenderResponse(result)
    case other =>
      log.error(s"Invalid message: $other")
  }

  override def postStop(): Unit = {
    scriptContext.close()
  }
}

private[server] object RenderActor {

  def props(sources: Seq[ScriptSource]): Props = Props(new RenderActor(sources))
}