package ru.dgolubets.reactjs.server.util

import java.io.File
import java.nio.file.ClosedWatchServiceException

import better.files.FileMonitor

import scala.concurrent.ExecutionContext

/**
  * FileMonitor with some fixes
  */
private[server] class FileMonitorEx(root: File, recursive: Boolean = true)
  extends FileMonitor(better.files.File(root.toPath), recursive) {

  override def start()(implicit executionContext: ExecutionContext): Unit = {
    watch(better.files.File(root.toPath), if (recursive) Int.MaxValue else 0)
    executionContext.execute(() => {
      try {
        while (true) {
          process(service.take())
        }
      } catch {
        case _: ClosedWatchServiceException => // no need to throw it
      }
    })
  }
}
