name := "trabebar-akka-stream"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test