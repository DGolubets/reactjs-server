package ru.dgolubets.reactjs.server.script

import com.typesafe.scalalogging.LazyLogging
import ru.dgolubets.reactjs.server.script.graal.GraalScriptContext
import ru.dgolubets.reactjs.server.script.nashorn.NashornScriptContext

private[server] trait ScriptContext extends AutoCloseable {

  def eval(code: String): ScriptValue

  def eval(source: ScriptSource): ScriptValue

  def exportSymbol(name: String, value: Any): Unit
}

private[server] object ScriptContext extends LazyLogging {
  def apply(): ScriptContext = {
    try {
      GraalScriptContext()
    } catch {
      case e: Throwable =>
        logger.warn(s"Cannot create Graal script context, falling back to Nashorn.", e)
        NashornScriptContext()
    }
  }
}

