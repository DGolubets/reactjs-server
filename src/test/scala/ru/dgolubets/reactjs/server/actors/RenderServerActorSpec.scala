package ru.dgolubets.reactjs.server.actors

import java.io.File
import java.nio.file.Files

import io.circe.Json
import org.scalatest._
import ru.dgolubets.reactjs.server._

import scala.language.postfixOps

/**
  * Integration tests for RenderActor actor.
  */
class RenderServerActorSpec extends WordSpec with ActorSpecLike with Matchers with BeforeAndAfterEach {

  import Messages._

  val tempDir = Files.createTempDirectory("RenderServerActorSpec")

  val renderSource = ScriptSource.fromString(
    """
      |function render(state){
      |  return "<div></div>"
      |}
    """.stripMargin)

  override def afterAll(): Unit = {
    super.afterAll()
    better.files.File(tempDir).delete(true)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    better.files.File(tempDir).clear()
  }

  "RenderServerActor" when {

    "not watching" should {

      "render" in {
        disposableActor(RenderServerActor.props(RenderServerSettings(Seq(renderSource)))) { server =>
          server ! RenderRequest("render", Json.Null)
          expectMsgPF() {
            case RenderResponse(Right(_)) =>
          }
        }
      }
    }

    "watching" should {

      "postpone rendering until ready" in {
        val files = Seq(
          new File(tempDir.toFile, "file1")
        )

        val sources = Seq(renderSource) ++ files.map(ScriptSource.fromFile(_))

        disposableActor(RenderServerActor.props(RenderServerSettings(sources, watch = Some(tempDir.toFile)))) { server =>
          server ! RenderRequest("render", Json.Null)
          expectNoMessage()
          for (f <- files) {
            f.createNewFile()
          }
          expectMsgPF() {
            case RenderResponse(Right(_)) =>
          }
        }
      }

      "return success" in {

        val sources = Seq(renderSource)

        disposableActor(RenderServerActor.props(RenderServerSettings(sources, watch = Some(tempDir.toFile)))) { server =>

          Thread.sleep(100) // wait for source monitor to report

          server ! RenderRequest("render", Json.Null)

          expectMsgPF() {
            case RenderResponse(Right(_)) =>
          }
        }
      }
    }
  }
}
