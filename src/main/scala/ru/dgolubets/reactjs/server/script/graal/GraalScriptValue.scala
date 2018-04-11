package ru.dgolubets.reactjs.server.script.graal

import org.graalvm.polyglot.Value
import ru.dgolubets.reactjs.server.script.ScriptValue

case class GraalScriptValue(value: Value) extends ScriptValue {
  override def asString(): String = value.asString()
}