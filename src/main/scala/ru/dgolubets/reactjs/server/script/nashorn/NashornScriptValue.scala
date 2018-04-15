package ru.dgolubets.reactjs.server.script.nashorn

import ru.dgolubets.reactjs.server.script.ScriptValue

private[server] case class NashornScriptValue(value: AnyRef) extends ScriptValue {
  override def asString(): String = value.asInstanceOf[String]
}
