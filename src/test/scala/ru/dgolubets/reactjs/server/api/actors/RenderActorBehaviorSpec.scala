package ru.dgolubets.reactjs.server.api.actors


import javax.script.ScriptEngine

import akka.pattern._
import akka.testkit.TestActorRef
import akka.util.Timeout
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import ru.dgolubets.jsmoduleloader.api._
import ru.dgolubets.reactjs.server.internal.RenderLogic

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
 * Unit tests for RenderActor actor.
 */
class RenderActorBehaviorSpec extends WordSpec with Matchers with MockFactory with ActorSpecLike {
  "RenderActor" when {

    "using async loader" should {

      trait Test {
        // mocks
        val renderLogicMock = mock[RenderLogic]
        val loaderMock = mock[AsyncScriptModuleLoader]
        val engineMock = mock[ScriptEngine]

        // let engine property free access
        (loaderMock.engine _).expects().returns(engineMock).anyNumberOfTimes()

        // timeout for ask pattern
        implicit val timeout = Timeout(10 seconds)
      }

      "call render logic" in new Test {

        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))
        val renderRequest = RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))
        val expectedResponse = RenderActor.Response("<div></div>")

        inSequence {

          (loaderMock.requireAsync(_: Seq[String]))
            .expects(Seq("React", renderRequest.module))
            .returns(Future.successful(Seq(ScriptModule(null), ScriptModule(null))))

          // async renderer should lock loader before rendering
          (loaderMock.lock[Any](_: () => Any)).expects(*).onCall { r: (() => Any) => r() }

          (renderLogicMock.render _).expects(where { case _ => true }).returns(Success(expectedResponse.html))

        }

        val result = renderer ? renderRequest

        result.value.get shouldBe Success(expectedResponse)
      }

      "return failure on module load error" in new Test {
        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))

        (loaderMock.requireAsync(_: Seq[String])).expects(*).returns(Future.failed(new Exception("Some error")))

        val result = renderer ? RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))

        assert(result.value.get.isFailure)
      }

      "return failure on render error" in new Test {
        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))
        val renderRequest = RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))

        inSequence {

          (loaderMock.requireAsync(_: Seq[String]))
            .expects(Seq("React", renderRequest.module))
            .returns(Future.successful(Seq(ScriptModule(null), ScriptModule(null))))

          // async renderer should lock loader before rendering
          (loaderMock.lock[Any](_: () => Any)).expects(*).onCall { r: (() => Any) => r() }

          (renderLogicMock.render _).expects(where { case _ => true }).returns(Failure(new Exception("Some error")))

        }

        val result = renderer ? renderRequest

        assert(result.value.get.isFailure)
      }
    }

    "using sync loader" should {

      trait Test {
        // mocks
        val renderLogicMock = mock[RenderLogic]
        val loaderMock = mock[SyncScriptModuleLoader]
        val engineMock = mock[ScriptEngine]

        // let engine property free access
        (loaderMock.engine _).expects().returns(engineMock).anyNumberOfTimes()

        // timeout for ask pattern
        implicit val timeout = Timeout(10 seconds)
      }

      "call render logic" in new Test {

        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))
        val renderRequest = RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))
        val expectedResponse = RenderActor.Response("<div></div>")

        (loaderMock.require(_: String)).expects("React").returns(Success(ScriptModule(null)))
        (loaderMock.require(_: String)).expects(renderRequest.module).returns(Success(ScriptModule(null)))

        (renderLogicMock.render _).expects(where {case _ => true}).returns(Success(expectedResponse.html))

        val result = renderer ? renderRequest

        result.value.get shouldBe Success(expectedResponse)
      }

      "return failure on module load error" in new Test {
        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))

        (loaderMock.require(_: String)).expects(*).returns(Failure(new Exception("Some error")))

        val result = renderer ? RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))

        assert(result.value.get.isFailure)
      }

      "return failure on render error" in new Test {
        val renderer = TestActorRef(new RenderActor(loaderMock, renderLogicMock))
        val renderRequest = RenderActor.Request("SomeClass", None, Map("prop1" -> "val1"))

        (loaderMock.require(_: String)).expects("React").returns(Success(ScriptModule(null)))
        (loaderMock.require(_: String)).expects(renderRequest.module).returns(Success(ScriptModule(null)))

        (renderLogicMock.render _).expects(where {case _ => true}).returns(Failure(new Exception("Some error")))

        val result = renderer ? renderRequest

        assert(result.value.get.isFailure)
      }
    }
  }
}
