Common.appSettings

name := """multi-domain-play-java"""

lazy val common = (project in file("modules/common")).enablePlugins(PlayJava)

lazy val web = (project in file("modules/web")).enablePlugins(PlayJava).dependsOn(common)

lazy val admin = (project in file("modules/admin")).enablePlugins(PlayJava).dependsOn(common,web)

lazy val root = (project in file(".")).enablePlugins(PlayJava).aggregate(common,admin, web).dependsOn(common, admin, web)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "be.objectify" %% "deadbolt-java" % "2.3.2"
)

