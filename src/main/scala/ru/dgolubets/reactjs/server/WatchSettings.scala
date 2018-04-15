package ru.dgolubets.reactjs.server

import java.io.File

import scala.concurrent.duration._
import scala.language.postfixOps

case class WatchSettings(root: File, delay: FiniteDuration = 0.5 seconds)