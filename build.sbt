name := "AkkaMainAssignment"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.3" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "2.8.47" % "test"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test
)

coverageEnabled:=true
