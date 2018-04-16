package ru.dgolubets.reactjs.server.actors

import java.io.File
import java.nio.file.{Path, WatchEvent}

import scala.concurrent.duration._

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import better.files.{File => BetterFile}

import ru.dgolubets.reactjs.server.util.{ExecutionContexts, FileMonitorEx}

private[server] class SourcesMonitorActor(server: ActorRef, root: File, files: Seq[File], delay: FiniteDuration) extends Actor {

  import context.dispatcher

  import Messages._
  import SourcesMonitorActor._

  private val watcher = new FileMonitorEx(root, recursive = true) {

    private val scheduler = context.system.scheduler
    private var scheduledNotification: Option[Cancellable] = None

    override def onEvent(eventType: WatchEvent.Kind[Path], file: BetterFile, count: Int): Unit = {

      for (n <- scheduledNotification) {
        n.cancel()
      }

      val c = scheduler.scheduleOnce(delay) {
        self ! FileChanged
      }

      scheduledNotification = Some(c)
    }
  }

  watcher.start()(ExecutionContexts.blockingIO)

  private var filesModified: Map[File, Option[FileInfo]] = getFilesModified()
  notifyServer(filesModified, None)

  override def postStop(): Unit = {
    watcher.close()
  }

  def getFilesModified(): Map[File, Option[FileInfo]] = {
    files.map { file =>
      if (file.exists()) {
        val info = FileInfo(file.lastModified(), BetterFile(file.toPath).md5)
        file -> Some(info)
      } else file -> None
    }.toMap
  }

  def notifyServer(current: Map[File, Option[FileInfo]], previous: Option[Map[File, Option[FileInfo]]]): Unit = {
    val missingFiles = current
      .collect { case (k, v) if v.isEmpty => k }
      .toList

    if (missingFiles.nonEmpty) {
      server ! SourcesMissing(missingFiles)
    }
    else {
      val updatedFiles = previous
        .fold(current) { prev =>
          current.filter {
            case (k, Some(v)) => prev.get(k).forall(_.forall(_ != v))
            case _ => false
          }
        }
        .keys
        .toList
      if (previous.isEmpty || updatedFiles.nonEmpty) {
        server ! SourcesChanged(updatedFiles)
      }
    }
  }

  override def receive: Receive = {
    case FileChanged =>
      val newFilesModified = getFilesModified()
      val oldFilesModified = filesModified

      if (oldFilesModified != newFilesModified) {
        filesModified = newFilesModified
        notifyServer(newFilesModified, Some(oldFilesModified))
      }
  }
}


private[server] object SourcesMonitorActor {
  def props(server: ActorRef, root: File, files: Seq[File], delay: FiniteDuration): Props =
    Props(new SourcesMonitorActor(server, root, files, delay))

  object FileChanged

  case class FileInfo(lastModified: Long, md5: String)
}
