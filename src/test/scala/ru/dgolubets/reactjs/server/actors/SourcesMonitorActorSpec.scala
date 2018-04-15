package ru.dgolubets.reactjs.server.actors

import java.io.File
import java.nio.file.Files

import akka.testkit.TestProbe
import org.scalatest._
import ru.dgolubets.reactjs.server.actors.Messages.{SourcesChanged, SourcesMissing}

import scala.language.postfixOps

class SourcesMonitorActorSpec extends WordSpec with ActorSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  val tempDir = Files.createTempDirectory("SourcesMonitorActorSpec")

  override def afterAll(): Unit = {
    super.afterAll()
    better.files.File(tempDir).delete(true)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    better.files.File(tempDir).clear()
  }

  "SourcesMonitorActor" when {

    "started" should {

      "notify server of existing sources even if list is empty" in {
        val probe = TestProbe()
        val files = Nil
        disposableActor(SourcesMonitorActor.props(probe.ref, tempDir.toFile, files)) { _ =>
          probe.expectMsgPF() {
            case SourcesChanged(Nil) =>
          }
        }
      }

      "notify server of missing sources" in {
        val probe = TestProbe()
        val files = List(new File(tempDir.toFile, "non_existing_file"))
        disposableActor(SourcesMonitorActor.props(probe.ref, tempDir.toFile, files)) { _ =>
          probe.expectMsgPF() {
            case SourcesMissing(`files`) =>
          }
        }
      }
    }

    "file has been created" should {

      "notify server" in {
        val probe = TestProbe()
        val files = List(
          new File(tempDir.toFile, "file1"),
          new File(tempDir.toFile, "file2")
        )

        disposableActor(SourcesMonitorActor.props(probe.ref, tempDir.toFile, files)) { _ =>

          probe.expectMsgPF() {
            case SourcesMissing(`files`) =>
          }

          for (f <- files) {
            f.createNewFile()
          }

          probe.expectMsgPF() {
            case SourcesChanged(`files`) =>
          }
        }
      }
    }

    "file has been changed" should {

      "notify server" in {
        val probe = TestProbe()
        val files = List(
          new File(tempDir.toFile, "file1"),
          new File(tempDir.toFile, "file2")
        )

        for (f <- files) {
          f.createNewFile()
        }

        disposableActor(SourcesMonitorActor.props(probe.ref, tempDir.toFile, files)) { _ =>
          probe.expectMsgPF() {
            case SourcesChanged(`files`) =>
          }

          val changedFile = files.head
          better.files.File(changedFile.toPath).writeText("abc")

          probe.expectMsgPF() {
            case SourcesChanged(List(`changedFile`)) =>
          }
        }
      }
    }

    "file has been removed" should {

      "notify server" in {
        println("TEST")
        val probe = TestProbe()
        val files = List(
          new File(tempDir.toFile, "file1"),
          new File(tempDir.toFile, "file2")
        )

        for (f <- files) {
          f.createNewFile()
        }

        disposableActor(SourcesMonitorActor.props(probe.ref, tempDir.toFile, files)) { _ =>
          probe.expectMsgPF() {
            case SourcesChanged(`files`) =>
          }

          val deletedFile = files.head
          deletedFile.delete()

          probe.expectMsgPF() {
            case SourcesMissing(List(`deletedFile`)) =>
          }
        }
      }
    }
  }
}
