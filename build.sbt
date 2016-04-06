name := "akka-http"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2",
  "com.typesafe" % "config" % "1.3.0",
  "org.specs2" %% "specs2-core" % "3.7.2" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"
