import sbt._

object Dependencies {

  object Akka {
    private val version = "2.6.4"

    val actors = "com.typesafe.akka" %% "akka-actor" % version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
    val sl4j = "com.typesafe.akka" %% "akka-slf4j" % version
  }

  object Graal {
    val sdk = "org.graalvm.sdk" % "graal-sdk" % "20.0.0"
  }

  object Circe {
    private val version = "0.12.3"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser = "io.circe" %% "circe-parser" % version
  }

  object BetterFiles {
    private def version(scalaVersion: String) = if(scalaVersion.startsWith("2.13.")) "3.8.0" else "3.6.0"

    def core(scalaVersion: String) = "com.github.pathikrit" %% "better-files" % version(scalaVersion)
    def akka(scalaVersion: String) = "com.github.pathikrit"  %% "better-files-akka" % version(scalaVersion)
  }

  val typesafeConfig = "com.typesafe" % "config" % "1.3.3"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
  val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
}
