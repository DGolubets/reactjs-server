package ru.dgolubets.reactjs.server

import java.io.File

import scala.concurrent.duration._

case class RenderServerSettings(sources: Seq[ScriptSource],
                                watch: Option[File] = None,
                                nInstances: Int = Runtime.getRuntime.availableProcessors,
                                requestTimeout: FiniteDuration = 30 seconds)
