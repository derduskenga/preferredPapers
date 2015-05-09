import sbt._
import Keys._
import play.PlayImport._
import com.typesafe.sbt.web.SbtWeb.autoImport.{Assets, pipelineStages}
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.rjs.Import.{rjs, RjsKeys}
import com.typesafe.sbt.digest.Import.digest

object Common {
	def appName = "Timely Essay"
	
	// Common settings for every project
	def settings (theName: String) = Seq(
		name := theName,
		organization := "com.myweb",
		version := "1.0-SNAPSHOT",
		scalaVersion := "2.11.1",
		doc in Compile <<= target.map(_ / "none"),
		scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls")
	)
	// Settings for the app, i.e. the root project
	val appSettings = settings(appName)
	// Settings for every module, i.e. for every subproject
	def moduleSettings (module: String) = settings(module) ++: Seq(
		javaOptions in Test += s"-Dconfig.resource=application.conf"
	)
	// Settings for every service, i.e. for admin and web subprojects
	def serviceSettings (module: String) = moduleSettings(module) ++: Seq(
		includeFilter in (Assets, LessKeys.less) := "*.less",
		excludeFilter in (Assets, LessKeys.less) := "_*.less",
		pipelineStages := Seq(rjs, digest),
		RjsKeys.mainModule := s"main-$module"
	)
	
	val commonDependencies = Seq(
		cache,
		javaWs,
		ws,
		"org.webjars" % "jquery" % "2.1.1",
		"org.webjars" % "bootstrap" % "3.2.0",
		"org.webjars" % "requirejs" % "2.1.14-1",
		"com.google.code.gson" % "gson" % "2.3.1",
		"com.googlecode.json-simple"%"json-simple"%"1.1",
		"org.codehaus.jackson"%"jackson-mapper-asl"%"1.5.0",
		javaJdbc,
		javaEbean,
		"be.objectify" %% "deadbolt-java" % "2.3.2",
		"commons-io" % "commons-io" % "2.3",
		"commons-codec" % "commons-codec" % "1.6"
		// Add here more common dependencies:
		// jdbc,
		// anorm,
		// ...
	)
}