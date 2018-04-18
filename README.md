# reactjs-server
Renders React.js markup (or any other string) on JVM with Graal or Nashorn.

## Features
* Graal.js on GraalVM with fallback to Nashorn on HotSpot
* Multiple render instances
* Watches source files and reloads automatically

## Setup
```
resolvers += Resolver.bintrayRepo("dgolubets", "releases")
libraryDependencies += "ru.dgolubets" %% "reactjs-server" % "0.2"
```

## Minimal example
```scala
import akka.actor.ActorSystem
import io.circe.Json

import ru.dgolubets.reactjs.server._

implicit val system = ActorSystem()

import system.dispatcher

val renderSource = ScriptSource.fromString(
  """
    |function render(state){
    |  return "<h1>state.title</h1>"
    |}
  """.stripMargin)

val renderServer = new RenderServer(RenderServerSettings(Seq(renderSource)))

renderServer.render("render", Json.obj("title" -> Json.fromString("Some title"))).map { html =>
  println(html)
}
```

## Using Graal.js
You don't have to do anything but to start your application with [GraalVM](http://www.graalvm.org/). ReactJS server will try to instantiate Graal.js context by default and if it's not available - will fallback to using Nashorn.

## Providing polyfills
ReactJS server comes with a minimal set of polyfills for logging via ```console```.
The server will execute specified sources in the order provided, so it's easy to include your own polyfills by just adding them before your main script.

## Watching file sources
To watch for source file changes, specify watch settings with root pointing to some parent directory of your source files.
```scala
val renderSource = ScriptSource.fromFile(new File("path/to/my/source.js");

val watchSettings = WatchSettings(
  root = new File("path/to"), // watch root
  delay = 1 second // delay to aggregate file changes
)

val renderServer = new RenderServer(RenderServerSettings(
  Seq(renderSource), 
  watch = Some(watchSettings)
)
```

## Starting multiple instances
By default the server is gonna start an instance per CPU core, but you can override it.
```scala
val renderServer = new RenderServer(RenderServerSettings(
  Seq(renderSource), 
  nInstances = 2
)
```

## Performance
Both Graal.js and Nashorn can reach Node.js performance after a warmup (100-1000+ iterations per instance). Warm up times are likely to be improved in future Graal versions.

## Using with Webpack
Create a separate configuration to bundle a [library](https://webpack.js.org/configuration/output/#output-library):
```javascript
output: {
  filename: "scripts/server.js",
  library: "mylib",
  libraryTarget: "var"
}
```
Export a render function in your index source file:
```javascript
export function renderToString(state: any): string {
    return ReactDOM.renderToString(<Root />);
}
```
