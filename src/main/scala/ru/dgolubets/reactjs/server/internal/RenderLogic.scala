package ru.dgolubets.reactjs.server.internal

import javax.script.ScriptEngine

import jdk.nashorn.api.scripting.ScriptObjectMirror
import ru.dgolubets.jsmoduleloader.api.ScriptModule

import scala.util.Try

/**
 * Encapsulates logic for rendering React.js class modules
 */
private[server] class RenderLogic {

  /**
   * Renders React.js class HTML.
   * @param engine Script engine
   * @param react React module
   * @param reactClass React class module
   * @param export Optional export name of the class from the module
   * @param props Properties of the react element
   * @return String HTML
   */
  def render(engine: ScriptEngine, react: ScriptModule, reactClass: ScriptModule, export: Option[String], props: Map[String, Any]): Try[String] = Try {
    val bindings = engine.createBindings()
    for (arg <- props) {
      bindings.put(arg._1, arg._2)
    }
    val reactElement = react.value.asInstanceOf[ScriptObjectMirror].callMember("createElement", reactClass.value, bindings)
    react.value.asInstanceOf[ScriptObjectMirror].callMember("renderToString", reactElement).toString
  }

}
