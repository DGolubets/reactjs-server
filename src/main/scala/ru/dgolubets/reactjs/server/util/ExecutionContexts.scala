package ru.dgolubets.reactjs.server.util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object ExecutionContexts {
  val blockingIO: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}
