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
    "org.specs2" %% "specs2-core" % "4.0.3" % Test,
    "org.neo4j.driver" % "neo4j-java-driver" % "1.6.1"
  ))
  .settings(
    scalacOptions ++= Seq("-deprecation", "-feature"),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )