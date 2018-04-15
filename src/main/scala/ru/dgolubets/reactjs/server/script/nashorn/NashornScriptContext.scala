package ru.dgolubets.reactjs.server.script.nashorn

import javax.script.ScriptEngine
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import ru.dgolubets.reactjs.server.script.{ScriptContext, ScriptSource, ScriptValue}

import scala.language.implicitConversions

private[server] class NashornScriptContext(engine: ScriptEngine) extends ScriptContext {

  import NashornScriptContext._

  override def eval(code: String): ScriptValue = NashornScriptValue(engine.eval(code))

  override def eval(source: ScriptSource): ScriptValue = {
    val reader = source.reader
    try {
      engine.eval(reader)
    }
    finally {
      reader.close()
    }
  }

  override def exportSymbol(name: String, value: Any): Unit = {
    engine.put(name, value)
  }

  override def close(): Unit = {}
}

private[server] object NashornScriptContext {

  private implicit def wrapValue(value: AnyRef): ScriptValue = NashornScriptValue(value)

  def apply(): NashornScriptContext = {
    val factory = new NashornScriptEngineFactory
    val engine = factory.getScriptEngine("--language=es6")
    new NashornScriptContext(engine)
  }
}