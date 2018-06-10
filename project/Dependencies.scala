import sbt._

object Dependencies {

  object Akka {
    private val version = "2.5.11"

    val actors = "com.typesafe.akka" %% "akka-actor" % version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
    val sl4j = "com.typesafe.akka" %% "akka-slf4j" % version
  }

  object Graal {
    val sdk = "org.graalvm" % "graal-sdk" % "1.0.0-rc2"
  }

  object Circe {
    private val version = "0.9.1"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser = "io.circe" %% "circe-parser" % version
  }

  object BetterFiles {
    private val version = "3.4.0"
    val core = "com.github.pathikrit" %% "better-files" % version
    val akka = "com.github.pathikrit"  %% "better-files-akka" % version
  }

  val typesafeConfig = "com.typesafe" % "config" % "1.3.3"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"
  val scalaMock = "org.scalamock" %% "scalamock" % "4.1.0"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
}
