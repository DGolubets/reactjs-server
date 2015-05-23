package ru.dgolubets.reactjs.server.api

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import ru.dgolubets.jsmoduleloader.api.ScriptModuleLoader
import ru.dgolubets.reactjs.server.api.actors.RenderActor

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * React.js server.
 * Creates a new daemon ActorSystem with a number of RenderActor actors.
 *
 * @param loaderFactory Script loader constructor
 *                      Should be able to find React and all other class modules.
 */
class ReactServer(loaderFactory: => ScriptModuleLoader,
                  customConfig: Config = ConfigFactory.load().getConfig("ru.dgolubets.reactjs.server")) extends LazyLogging {

  // this line helps to init logging before we start multiple akka threads
  // and thus prevents 'substitute loggers' message
  logger.trace(s"Starting ReactServer..")

  // library scoped config
  private val config = {
    // set default nr-of-instances to the number of CPU cores
    val dynamicConfig = ConfigFactory.parseString(s"nr-of-instances = ${Runtime.getRuntime.availableProcessors}")
    customConfig.withFallback(dynamicConfig)
  }

  // akka actor system
  private lazy val system = {
    val defaultConfig = ConfigFactory.load()

    // apply akka settings from the library config first, then default settings
    val akkaConfig = config.withFallback(defaultConfig)

    ActorSystem("ru-dgolubets-reactjs-server", akkaConfig)
  }

  // router to renderers
  private lazy val renderer = {
    val nOfInstances = config.getInt("nr-of-instances")
    system.actorOf(RoundRobinPool(nOfInstances).props(RenderActor.props(loaderFactory)), "router")
  }

  // timeout for 'ask pattern'
  implicit val timeout = Timeout(config.getDuration("timeout", TimeUnit.SECONDS), TimeUnit.SECONDS)

  /**
   * Renders React.js element HTML.
   * @param element React element
   * @return
   */
  def render(element: ReactElement): Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    (renderer ? element.toRendererRequest).mapTo[RenderActor.Response].map(_.html)
  }

  /**
   * Shuts down the server.
   *
   * The actor system created by server is daemon and will shutdown automatically when JVM exits.
   * Though it may be necessary to stop the server along, without exiting JVM.
   * That's why this method does exist.
   */
  def shutdown() = system.shutdown()
}

object ReactServer {
  def apply(loaderFactory: => ScriptModuleLoader) = new ReactServer(loaderFactory)
  def apply(loaderFactory: => ScriptModuleLoader, customConfig: Config) = new ReactServer(loaderFactory, customConfig)
}




