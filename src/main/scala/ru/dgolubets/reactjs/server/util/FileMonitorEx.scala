package ru.dgolubets.reactjs.server.util

import java.io.File
import java.nio.file.ClosedWatchServiceException

import scala.concurrent.ExecutionContext

import better.files.FileMonitor

/**
  * FileMonitor with some fixes
  */
private[server] class FileMonitorEx(root: File, recursive: Boolean = true)
  extends FileMonitor(better.files.File(root.toPath), recursive) {

  override def start()(implicit executionContext: ExecutionContext): Unit = {
    watch(better.files.File(root.toPath), if (recursive) Int.MaxValue else 0)
    executionContext.execute(new Runnable {
      override def run(): Unit = {
        try {
          while (true) {
            process(service.take())
          }
        } catch {
          case _: ClosedWatchServiceException => // no need to throw it
        }
      }
    })
  }
}
