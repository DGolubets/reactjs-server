package ru.dgolubets.reactjs.server.actors

import java.io.File

import io.circe.Json

private[server] object Messages {

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

  /**
    * Signal that all source files exist
    * and one or more files has been changes
    * @param files created or changed files
    */
  case class SourcesChanged(files: Seq[File])

  /**
    * Signal that some source files are missing
    * @param files missing files
    */
  case class SourcesMissing(files: Seq[File])
}
