package ru.dgolubets.reactjs.server.util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

private[server] object ExecutionContexts {
  val blockingIO: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}
