package ru.dgolubets.reactjs.server.api

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import org.scalatest.{WordSpec, Matchers}
import ru.dgolubets.jsmoduleloader.api.amd.AmdLoader
import ru.dgolubets.jsmoduleloader.api.commonjs.CommonJsLoader
import ru.dgolubets.jsmoduleloader.api.readers.FileModuleReader


import scala.concurrent.Future

/**
 * ReactServer tests.
 */
class ReactServerSpec extends WordSpec with Matchers with ScalaFutures {
  import scala.concurrent.ExecutionContext.Implicits.global

  "ReactServer" when {
    "uses AMD loader" should {
      "pass stress test" in {

        val engine = new ReactServer(AmdLoader(FileModuleReader("src/test/javascript/amd")))
        val requests = for (i <- 1 to 1000) yield {
          engine.render(CommentBox("http://comments.org", 1000))
        }

        val result = Future.sequence(requests)
        result.onComplete {
          case _ => engine.shutdown()
        }

        whenReady(result, Timeout(Span(60, Seconds))){ _ =>
        }

      }
    }

    "uses CommonJs loader" should {
      "pass stress test" in {

        val engine = new ReactServer(CommonJsLoader(FileModuleReader("src/test/javascript/commonjs")))
        val requests = for (i <- 1 to 1000) yield {
          engine.render(CommentBox("http://comments.org", 1000))
        }

        val result = Future.sequence(requests)
        result.onComplete {
          case _ => engine.shutdown()
        }

        whenReady(result, Timeout(Span(60, Seconds))){ _ =>
        }

      }
    }
  }

  case class CommentBox(url: String, pollingInterval: Int) extends ReactElement {

    override val reactClass = ReactClass("CommentBox")

    override def props = Map("url" -> url, "pollingInterval" -> pollingInterval)

  }
}

