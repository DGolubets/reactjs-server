package ru.dgolubets.reactjs.server.api

import ru.dgolubets.reactjs.server.api.actors.RenderActor

/**
 * React.js element.
 */
trait ReactElement {

  /**
   * React.js class of this element.
   * @return
   */
  val reactClass: ReactClass

  /**
   * Gets properties to render.
   * @return
   */
  def props: Map[String, Any]

  /**
   * Creates render request.
   * @return
   */
  def toRendererRequest = RenderActor.Request(reactClass.module, reactClass.exportName, props)
}
