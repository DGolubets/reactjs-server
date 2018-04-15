package ru.dgolubets.reactjs.server.script

import java.io._
import java.nio.charset.{Charset, StandardCharsets}

trait ScriptSource {
  def reader: Reader
}

object ScriptSource {

  def fromString(code: String): ScriptSource =
    StringScriptSource(code)

  def fromFile(file: File,
               charset: Charset = StandardCharsets.UTF_8): ScriptSource =
    FileScriptSource(file, charset)

  def fromResource(name: String,
                   classLoader: ClassLoader = Thread.currentThread.getContextClassLoader,
                   charset: Charset = StandardCharsets.UTF_8): ScriptSource =
    ResourceScriptSource(name, classLoader, charset)
}

