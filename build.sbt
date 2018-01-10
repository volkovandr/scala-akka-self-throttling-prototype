name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "io.prometheus" % "simpleclient"            % "0.0.26",
  "io.prometheus" % "simpleclient_httpserver" % "0.0.26",
  "com.typesafe"                % "config"          % "1.3.1"
)
