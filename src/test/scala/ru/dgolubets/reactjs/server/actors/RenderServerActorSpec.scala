package ru.dgolubets.reactjs.server.actors

import java.io.{File, PrintWriter}
import java.nio.file.Files

import scala.language.postfixOps
import io.circe.Json
import org.scalatest._
import ru.dgolubets.reactjs.server._

/**
 * Integration tests for RenderActor actor.
 */
class RenderServerActorSpec extends WordSpec with ActorSpecLike with Matchers with BeforeAndAfterEach {

  import Messages._

  val tempDir = Files.createTempDirectory("RenderServerActorSpec")
  val watchSettings = WatchSettings(tempDir.toFile)

  val renderScript =
    """
      |function render(state){
      |  return "<div></div>"
      |}
    """.stripMargin
  val renderSource = ScriptSource.fromString(renderScript)

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

        disposableActor(RenderServerActor.props(RenderServerSettings(sources, watch = Some(watchSettings)))) { server =>
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

        disposableActor(RenderServerActor.props(RenderServerSettings(sources, watch = Some(watchSettings)))) { server =>

          Thread.sleep(100) // wait for source monitor to report

          server ! RenderRequest("render", Json.Null)

          expectMsgPF() {
            case RenderResponse(Right(_)) =>
          }
        }
      }

      "if render actors fail to initialize wait for sources to change" in {

        val fakeSource = new File(tempDir.toFile, "fake_source")
        fakeSource.createNewFile()

        def overwriteSource(text: String): Unit = {
          val writer = new PrintWriter(fakeSource)
          writer.println(text)
          writer.close()
        }

        overwriteSource("(gibberish that should make it fail to load {")

        val sources = Seq(ScriptSource.fromFile(fakeSource))

        disposableActor(RenderServerActor.props(RenderServerSettings(sources, watch = Some(watchSettings)))) { server =>

          // this should not produce any result yet, cos renderer should have failed to load
          server ! RenderRequest("render", Json.Null)
          expectNoMessage()

          // update the source with valid script
          overwriteSource(renderScript)

          // we should get our response
          server ! RenderRequest("render", Json.Null)
          expectMsgPF(hint = "should get response after the source is updated") {
            case RenderResponse(Right(_)) =>
          }
        }
      }
    }
  }
}
