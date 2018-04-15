package ru.dgolubets.reactjs.server.actors

import java.io.File
import java.nio.file.{Path, WatchEvent}

import akka.actor.{Actor, ActorRef, Props}
import better.files.{File => BetterFile}
import ru.dgolubets.reactjs.server.util.{ExecutionContexts, FileMonitorEx}

class SourcesMonitorActor(server: ActorRef, root: File, files: Seq[File]) extends Actor {

  import Messages._
  import SourcesMonitorActor._

  private val watcher = new FileMonitorEx(root, recursive = true) {
    override def onEvent(eventType: WatchEvent.Kind[Path], file: BetterFile, count: Int): Unit = {
      self ! FileChanged
    }
  }

  watcher.start()(ExecutionContexts.blockingIO)

  private var filesModified: Map[File, Option[Long]] = getFilesModified()
  notifyServer(filesModified, None)

  override def postStop(): Unit = {
    watcher.close()
  }

  def getFilesModified(): Map[File, Option[Long]] = {
    files.map { file =>
      if (file.exists()) {
        file -> Some(file.lastModified())
      } else file -> None
    }.toMap
  }

  def notifyServer(current: Map[File, Option[Long]], previous: Option[Map[File, Option[Long]]]): Unit = {
    val missingFiles = current
      .collect { case (k, v) if v.isEmpty => k }
      .toList

    if (missingFiles.nonEmpty) {
      server ! SourcesMissing(missingFiles)
    }
    else {
      val updatedFiles = previous
        .fold(current) { prev =>
          current.filter { case (k, Some(v)) => prev.get(k).forall(_.forall(_ < v)) }
        }
        .keys
        .toList
      if(previous.isEmpty || updatedFiles.nonEmpty) {
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


object SourcesMonitorActor {
  def props(server: ActorRef, root: File, files: Seq[File]): Props = Props(new SourcesMonitorActor(server, root, files))

  object FileChanged

}
