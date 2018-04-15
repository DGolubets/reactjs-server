package ru.dgolubets.reactjs.server

import java.io.File

import scala.concurrent.duration._
import scala.language.postfixOps

case class RenderServerSettings(sources: Seq[ScriptSource],
                                watch: Option[File] = None,
                                nInstances: Int = Runtime.getRuntime.availableProcessors,
                                requestTimeout: FiniteDuration = 120 seconds)
