package ru.dgolubets.reactjs.server.api

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{AsyncWordSpec, Matchers}
import ru.dgolubets.jsmoduleloader.api.amd.AmdLoader
import ru.dgolubets.jsmoduleloader.api.commonjs.CommonJsLoader
import ru.dgolubets.jsmoduleloader.api.readers.FileModuleReader


import scala.concurrent.Future

/**
 * ReactServer tests.
 */
class ReactServerSpec extends AsyncWordSpec with Matchers with ScalaFutures {

  "ReactServer" when {
    "uses AMD loader" should {
      "pass stress test" in {

        val engine = new ReactServer(AmdLoader(FileModuleReader("src/test/javascript/amd")))
        val requests = for (i <- 1 to 1000) yield {
          engine.render(CommentBox("http://comments.org", 1000))
        }

        Future.sequence(requests).map { _ =>
          engine.shutdown()
          succeed
        }
      }
    }

    "uses CommonJs loader" should {
      "pass stress test" in {

        val engine = new ReactServer(CommonJsLoader(FileModuleReader("src/test/javascript/commonjs")))
        val requests = for (i <- 1 to 1000) yield {
          engine.render(CommentBox("http://comments.org", 1000))
        }

        Future.sequence(requests).map { _ =>
          engine.shutdown()
          succeed
        }
      }
    }
  }

  case class CommentBox(url: String, pollingInterval: Int) extends ReactElement {

    override val reactClass = ReactClass("CommentBox")

    override def props = Map("url" -> url, "pollingInterval" -> pollingInterval)

  }
}

