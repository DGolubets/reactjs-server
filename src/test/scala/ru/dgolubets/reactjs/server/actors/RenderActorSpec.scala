package ru.dgolubets.reactjs.server.actors

import io.circe.Json
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import ru.dgolubets.reactjs.server.script.ScriptSource

import scala.language.postfixOps

/**
  * Integration tests for RenderActor actor.
  */
class RenderActorSpec extends WordSpec with ActorSpecLike with Matchers with ScalaFutures {

  import ru.dgolubets.reactjs.server.actors.Messages._

  "RenderActor" should {

    "provide console polyfill" in {

      val render = ScriptSource.fromString(
        """
          |function render(state){
          |  return "<div>" + state.content + "</div>"
          |}
        """.stripMargin)

      val test = ScriptSource.fromString(
        """
          |console.log("Normal log")
          |console.trace("Trace log")
          |console.debug("Debug log", "with more logs")
          |console.warn("Warn log")
          |console.error("Error log")
          |function render(state){ return "<div>" + state.content + "</div>" }
        """.stripMargin)

      val actor = system.actorOf(RenderActor.props(List(render, test)))

      actor ! RenderRequest("render", Json.obj("content" -> Json.fromString("Abc")))

      expectMsgPF(timeout) {
        case RenderResponse(Right("<div>Abc</div>")) =>
      }
    }
  }
}
