package ru.dgolubets.reactjs.server.api.actors

import akka.actor._
import org.scalatest._
import ru.dgolubets.jsmoduleloader.api.amd.AmdLoader
import ru.dgolubets.jsmoduleloader.api.commonjs.CommonJsLoader
import ru.dgolubets.jsmoduleloader.api.readers.FileModuleReader

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Integration tests for RenderActor actor.
 */
class RenderActorIntegrationSpec extends WordSpec with ActorSpecLike with Matchers {

  val timeout = 10 seconds

  "RenderActor" when {

    "using AMD" should {

      trait AmdTest {
        val loader = AmdLoader(FileModuleReader("src/test/javascript/amd"))
        val renderer = system.actorOf(RenderActor.props(loader))
      }

      "render" in new AmdTest {
        renderer ! RenderActor.Request("CommentBox", None, Map("prop1" -> "val1"))
        expectMsgType[RenderActor.Response](timeout)
      }

      "fail on error" in new AmdTest {
        renderer ! RenderActor.Request("NonExistentModule", None, Map("prop1" -> "val1"))
        expectMsgType[Status.Failure](timeout)
      }
    }

    "using CommonJs" should {

      trait CommonJsTest {
        val loader = CommonJsLoader(FileModuleReader("src/test/javascript/commonjs"))
        val renderer = system.actorOf(RenderActor.props(loader))
      }

      "render" in new CommonJsTest {
        renderer ! RenderActor.Request("CommentBox", None, Map("prop1" -> "val1"))
        expectMsgType[RenderActor.Response](timeout)
      }

      "fail on error" in new CommonJsTest {
        renderer ! RenderActor.Request("NonExistentModule", None, Map("prop1" -> "val1"))
        expectMsgType[Status.Failure](timeout)
      }
    }
  }
}
