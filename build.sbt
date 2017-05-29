name := "akkastreams"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
"com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
"com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
"com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
"com.typesafe.akka" %% "akka-testkit" % akkaVersion
)
        