package ru.dgolubets.reactjs.server.api.actors

import akka.actor.{Actor, Props, Status}
import akka.pattern._
import ru.dgolubets.jsmoduleloader.api._
import ru.dgolubets.reactjs.server.internal.RenderLogic

/**
 * Actor which renders React.js elements to HTMl.
 *
 * @param moduleLoader Script module loader to use
 * @param renderLogic Actual render code
 */
class RenderActor private[actors] (moduleLoader: ScriptModuleLoader, renderLogic: RenderLogic) extends Actor {
  import RenderActor._
  import context.dispatcher

  def this(moduleLoader: ScriptModuleLoader) = this(moduleLoader, new RenderLogic)

  // set actor behavior based on sync\async loader type
  moduleLoader match {
    case asyncLoader: AsyncScriptModuleLoader => context.become(asyncRenderer(asyncLoader))
    case syncLoader: SyncScriptModuleLoader => context.become(syncRenderer(syncLoader))
  }

  /**
   * Async rendering behavior
   * @param asyncLoader Script module loader
   * @return
   */
  private def asyncRenderer(asyncLoader: AsyncScriptModuleLoader): Receive = {
    case r: Request =>
      val htmlFuture = asyncLoader.requireAsync(Seq(reactModuleName, r.module)).map {
        case Seq(react, reactClass) =>
          // synchronisation is required cos we access native nashorn objects here
          moduleLoader.lock {
            val html = renderLogic.render(asyncLoader.engine, react, reactClass, r.export, r.props).get
            Response(html)
          }
      }
      htmlFuture.pipeTo(sender())
  }

  /**
   * Sync rendering behavior
   * @param syncLoader Script module loader
   * @return
   */
  private def syncRenderer(syncLoader: SyncScriptModuleLoader): Receive = {
    case r: Request =>
      try {
        val react = syncLoader.require(reactModuleName).get
        val reactClass = syncLoader.require(r.module).get
        val html = renderLogic.render(syncLoader.engine, react, reactClass, r.export, r.props).get
        sender() ! Response(html)
      }
      catch{
        case err: Exception => sender() ! Status.Failure(err)
      }
  }

  // replaced with another behavior at start
  override def receive = {
    case _ =>
  }
}

object RenderActor {

  private[actors] val reactModuleName = "react"

  /**
   * Configuration for RenderActor.
   *
   * @param moduleLoaderFactory Script module loader factory
   *                            Every RenderActor should have it's own instance of loader
   * @return
   */
  def props(moduleLoaderFactory: => ScriptModuleLoader) = Props {
    new RenderActor(moduleLoaderFactory)
  }

  /**
   * Request to render a react.js element.
   * @param module Script module
   * @param export Export name
   * @param props Element properties
   */
  case class Request(module: String, export: Option[String], props: Map[String, Any])

  /**
   * Response with rendered HTML
   * @param html Rendered HTML
   */
  case class Response(html: String)
}