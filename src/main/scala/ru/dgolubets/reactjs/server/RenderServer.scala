package ru.dgolubets.reactjs.server

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import ru.dgolubets.reactjs.server.actors.{Messages, RenderServerActor}

import scala.concurrent.Future

class RenderServer(settings: RenderServerSettings)(implicit val system: ActorSystem) extends LazyLogging {

  import Messages._
  import system.dispatcher

  private implicit val timeout: Timeout = Timeout(settings.requestTimeout)

  private lazy val server: ActorRef = system.actorOf(RenderServerActor.props(settings), "render-server")

  def render(functionName: String, state: Json): Future[String] = {
    (server ? RenderRequest(functionName, state)).flatMap {
      case RenderResponse(Right(html)) => Future.successful(html)
      case RenderResponse(Left(error)) => Future.failed(error)
    }
  }

  def shutdown(): Unit = {
    server ! PoisonPill
  }
}
