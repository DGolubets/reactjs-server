# reactjs-server
Renders React.js classes with Nashorn in JVM

## Setup
```
libraryDependencies += "ru.dgolubets" %% "reactjs-server" % "0.1.0"
```

## Usage
The easy way is to use ReactServer class. It uses Akka to create a simple routing system with few render actors (defaults to the number of CPU cores). It creates daemonic actor system that won't block JVM from exiting. However you may want to shut it down explicitly, e.g. when you work with Play recompilation.
```scala
val engine = new ReactServer(CommonJsLoader(FileModuleReader("src/test/javascript/")))
val futureString = engine.render(CommentBox("http://comments.org", 1000))
futureString.onComplete {
  case _ => engine.shutdown()
}
```
It looks for a module with absolute id 'react', so it should be available to the loader.

### Declaring React elements
One way is to implement ReactElement trait.
```scala
case class CommentBox(url: String, pollingInterval: Int) extends ReactElement {
  override val reactClass = ReactClass("./components/CommentBox")
  override def props = Map("url" -> url, "pollingInterval" -> pollingInterval)
}
```

Or if you want, you can use more dynamic way:
```scala
object MyReactClasses {
	val CommentBox = ReactClass("./components/CommentBox")
}

import MyReactClasses._
CommentBox.createElement("url" -> "some url", "pollingInterval" -> 120)
```

It is also possible to use RenderActor directly if you want.

## Performance
It takes the most of time to compile React.js and other scripts, obviously. This compilation will occur in a fresh ReactServer exacly the same numer of times as the number of render actors created (cpu cores number by default). This means it will take few calls to warm up.
