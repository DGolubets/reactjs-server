package ru.dgolubets.reactjs.server.script

import java.io.{File, FileInputStream, InputStreamReader, Reader}
import java.nio.charset.{Charset, StandardCharsets}

class FileScriptSource(file: File, charset: Charset = StandardCharsets.UTF_8) extends ScriptSource {
  override def reader: Reader = {
    val stream = new FileInputStream(file)
    new InputStreamReader(stream, charset)
  }
}
