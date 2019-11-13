name := "homework1"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.2" 
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-1.2-api" % "2.11.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.11.2"
// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.28" % Test

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies ++= Seq(
  "mysql"        % "mysql-connector-java" % "5.1.46",
  "com.typesafe" % "config"               % "1.3.2"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

unmanagedJars in Compile += file("lib/cloudsim-3.0.3.jar")

