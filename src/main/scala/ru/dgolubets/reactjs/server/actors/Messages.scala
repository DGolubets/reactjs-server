package ru.dgolubets.reactjs.server.actors

import io.circe.Json

object Messages {

  /**
    * Request to render a react.js markup.
    *
    * @param functionName rendering function
    * @param state        parameter to the function
    */
  case class RenderRequest(functionName: String, state: Json)

  /**
    * Response with rendered HTML or error
    */
  case class RenderResponse(result: Either[Throwable, String])
}
