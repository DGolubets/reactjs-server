name := "reactjs-server"

organization := "ru.dgolubets"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "ru.dgolubets" %% "js-module-loader" % "0.1.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

  // test
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "ch.qos.logback" % "logback-classic" % "1.1.1" % "test"
)

// stress tests can fail other integration tests if run in parallel
parallelExecution in Test := false