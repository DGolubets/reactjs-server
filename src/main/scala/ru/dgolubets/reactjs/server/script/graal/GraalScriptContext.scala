package ru.dgolubets.reactjs.server.script.graal

import org.graalvm.polyglot.{Context, Source, Value}
import ru.dgolubets.reactjs.server.script.{ScriptContext, ScriptSource, ScriptValue}

import scala.language.implicitConversions

private[server] class GraalScriptContext(context: Context) extends ScriptContext {

  import GraalScriptContext._

  override def eval(source: ScriptSource): ScriptValue = {
    val reader = source.reader
    try {
      val src = Source.newBuilder(lang, reader, "Unnamed").build()
      context.eval(src)
    }
    finally {
      reader.close()
    }
  }

  override def eval(code: String): ScriptValue = context.eval(lang, code)

  override def exportSymbol(name: String, value: Any): Unit = {
    context.getBindings(lang).putMember(name, value)
  }

  override def close(): Unit = context.close()
}

private[server] object GraalScriptContext {

  private val lang = "js"

  private implicit def wrapValue(value: Value): ScriptValue = GraalScriptValue(value)

  def apply(): GraalScriptContext = {
    val ctx = Context.newBuilder(lang).allowAllAccess(true).build()
    new GraalScriptContext(ctx)
  }
}