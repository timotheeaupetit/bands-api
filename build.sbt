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
    "org.typelevel" %% "cats-core" % "0.9.0",
    "org.neo4j.driver" % "neo4j-java-driver" % "1.6.1",
    "org.specs2" %% "specs2-core" % "4.0.3" % Test
  ))
  .settings(
    scalacOptions ++= Seq("-deprecation", "-feature"),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )