import Dependencies._

name := "reactjs-server"

organization := "ru.dgolubets.reactjs.server.dgolubets"

scalaVersion := "2.12.4"
crossScalaVersions := List("2.11.6", "2.12.4")
releaseCrossBuild := true

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.bintrayRepo("dgolubets", "releases")

libraryDependencies ++= Seq(
  Akka.actors,
  typesafeConfig,
  scalaLogging,
  Graal.sdk,
  Circe.core,
  BetterFiles.core,
  BetterFiles.akka,

  // test
  scalaTest % Test,
  scalaMock % Test,
  Akka.testkit % Test,
  Akka.sl4j % Test,
  logback % Test
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