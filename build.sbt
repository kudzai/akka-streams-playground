name := "akkastreams"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "net.manub" %% "scalatest-embedded-kafka" % "0.13.1" % "test", //exclude("log4j", "log4j"),
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
        