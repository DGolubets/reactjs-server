package ru.dgolubets.reactjs.server

import scala.concurrent.duration._
import scala.language.postfixOps

case class RenderServerSettings(sources: Seq[ScriptSource],
                                watch: Option[WatchSettings] = None,
                                nInstances: Int = Runtime.getRuntime.availableProcessors,
                                requestTimeout: FiniteDuration = 120 seconds)
