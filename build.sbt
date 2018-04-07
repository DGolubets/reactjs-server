name := "reactjs-server"

organization := "ru.dgolubets"

scalaVersion := "2.12.5"
crossScalaVersions := List("2.11.6", "2.12.5")
releaseCrossBuild := true

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.bintrayRepo("dgolubets", "releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "ru.dgolubets" %% "js-module-loader" % "0.1.1",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",

  // test
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.scalamock" %% "scalamock" % "4.1.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.11",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"
)

// stress tests can fail other integration tests if run in parallel
parallelExecution in Test := false

// publishing
bintrayRepository := "releases"
bintrayOrganization in bintray := Some("dgolubets")
bintrayPackageLabels := Seq("js", "react")
licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/DGolubets/reactjs-server"))
publishMavenStyle := true
publishArtifact in Test := false
developers := List(Developer(
  "dgolubets",
  "Dmitry Golubets",
  "dgolubets@gmail.com",
  url("https://github.com/DGolubets")))