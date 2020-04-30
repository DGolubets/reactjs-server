import Dependencies._

name := "reactjs-server"

organization := "ru.dgolubets"

scalaVersion := "2.12.4"
crossScalaVersions := List("2.12.4", "2.13.2")
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
  BetterFiles.core(scalaVersion.value),
  BetterFiles.akka(scalaVersion.value),

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
bintrayVcsUrl := Some("git@github.com/DGolubets/reactjs-server")
licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/DGolubets/reactjs-server"))
publishMavenStyle := true
publishArtifact in Test := false
developers := List(Developer(
  "dgolubets",
  "Dmitry Golubets",
  "dgolubets@gmail.com",
  url("https://github.com/DGolubets")))