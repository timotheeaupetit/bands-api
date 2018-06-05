import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(
  name := "bands-api",
  version := "0.1",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(parallelExecution := false)
  .settings(libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % "1.5.12",
    "com.typesafe.akka" %% "akka-http" % "10.1.1",
    "org.typelevel" %% "cats-core" % "0.9.0",
    "org.neo4j.driver" % "neo4j-java-driver" % "1.6.1",
    "org.webjars" % "swagger-ui" % "3.9.2",
    "org.webjars" % "webjars-locator" % "0.32-1",
    "org.specs2" %% "specs2-core" % "4.0.3" % Test
  ))
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor",
    "com.typesafe.akka" %% "akka-stream"
  ).map(_ % "2.5.9"))
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-java8"
  ).map(_ % "0.8.0"))
  .settings(
    scalacOptions ++= Seq("-deprecation", "-feature"),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
