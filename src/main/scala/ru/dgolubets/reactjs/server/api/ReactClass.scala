package ru.dgolubets.reactjs.server.api

import ru.dgolubets.reactjs.server.internal.GenericReactElement


object ReactClass {
  /**
   * Declares React.js class as an instance of a module.
   *
   * @param module Module name
   * @return
   */
  def apply(module: String) = new ReactClass(module)

  /**
   * Declares React.js class as an instance of a module export.
   *
   * @param module Module name
   * @param exportName Export name
   * @return
   */
  def apply(module: String, exportName: String) = new ReactClass(module, exportName)
}

/**
 * Declares React.js class.
 *
 * @param module Module name
 * @param exportName Export name or None to treat the whole module as a class.
 */
class ReactClass private (val module: String, val exportName: Option[String]) {

  /**
   * Declares React.js class as an instance of a module.
   *
   * @param module Module name
   * @return
   */
  def this(module: String) = this(module, None)

  /**
   * Declares React.js class as an instance of a module export.
   *
   * @param module Module name
   * @param exportName Export name
   * @return
   */
  def this(module: String, exportName: String) = this(module, Some(exportName))

  /**
   * Creates a generic element of this class.
   *
   * @param props Set of properties.
   * @return
   */
  def createElement(props: (String, Any)*) : ReactElement = GenericReactElement(this, props.toMap)
}

