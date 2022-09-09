import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(
  name := "bands-api",
  version := "0.1",
  scalaVersion := "2.12.6"
)

val akkaVersion = "2.5.9"
val akkaHttpVersion = "10.1.1"
val circeVersion = "0.10.0"
val specs2Version = "4.0.4"

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(parallelExecution := false)
  .settings(libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % "1.5.12",
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-circe" % "1.18.0",
    "net.ruippeixotog" % "scala-scraper_2.12" % "2.1.0",
    "org.typelevel" %% "cats-core" % "1.4.0",
    "org.neo4j.driver" % "neo4j-java-driver" % "4.1.0",
    "org.webjars" % "swagger-ui" % "3.9.2",
    "org.webjars" % "webjars-locator" % "0.32-1",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test
  ))
  .settings(libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core",
    "org.specs2" %% "specs2-mock"
  ).map(_ % specs2Version % Test))
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor",
    "com.typesafe.akka" %% "akka-stream"
  ).map(_ % akkaVersion))
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-java8"
  ).map(_ % circeVersion))
  .enablePlugins(JavaAppPackaging)

packageName in Universal := "bands-api"
