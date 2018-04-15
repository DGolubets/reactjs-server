package ru.dgolubets.reactjs.server.script

import java.io.{Reader, StringReader}

case class StringScriptSource(code: String) extends ScriptSource {
  override def reader: Reader = {
    new StringReader(code)
  }
}
