package ru.dgolubets.reactjs.server.internal

import ru.dgolubets.reactjs.server.api.{ReactClass, ReactElement}

/**
 * Generic ReactElement implementation for internal use.
 * @param reactClass
 * @param props
 */
private[server] case class GenericReactElement(reactClass: ReactClass, props: Map[String, Any]) extends ReactElement
