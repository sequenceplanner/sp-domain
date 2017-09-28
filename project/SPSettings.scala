import sbt._
import Keys._
import sbt.{Developer, ScmInfo, url}

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.typesafe.sbt.SbtPgp.autoImport._
import xerial.sbt.Sonatype.autoImport._

object SPSettings {

  val defaultBuildSettings = Seq(
    organization := PublishingSettings.orgNameFull,
    homepage     := Some(PublishingSettings.githubSP("")),
    licenses     := PublishingSettings.mitLicense,
    scalaVersion := versions.scala,
    scalacOptions ++= scalacOpt,
    resolvers ++= projectResolvers,
    useGpg := true,
    publishArtifact in Test := false,
    publishMavenStyle := true,
    publishTo := PublishingSettings.pubTo.value,
    pomIncludeRepository := { _ => false },
    sonatypeProfileName := PublishingSettings.groupIdSonatype,
    developers := List(
      Developer(
        id = "kristoferb",
        name = "kristofer Bengtsson",
        email = "kristofer@sekvensa.se",
        url   = url("https://github.com/kristoferB")
      ),
      Developer(
        id = "m-dahl",
        name = "Martin Dahl",
        email = "martin.dahl@chalmers.se",
        url   = url("https://github.com/m-dahl")
      ),
      Developer(
        id = "patrikm",
        name = "Patrik Bergagård",
        email = "Patrik.Bergagard@alten.se",
        url   = url("https://github.com/patrikm")
      ),
      Developer(
        id = "ashfaqfarooqui",
        name = "Ashfaq Farooqui",
        email = "ashfaqf@chalmers.se",
        url   = url("https://github.com/ashfaqfarooqui")
      )
    )
  )

  /** Options for the scala compiler */
  lazy val scalacOpt = Seq(
    //"-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps"
  )

  lazy val projectResolvers: Seq[Resolver] = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/Releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.12.3"
    val log4js = "1.4.10"
    val scalaTest = "3.0.1"
  }

  /**
    * These dependencies are shared between JS and JVM projects
    * the special %%% function selects the correct version for each project
    */
  lazy val domainDependencies = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
    "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.0.5",
    "com.typesafe.play" %%% "play-json" % "2.6.0",
    "org.julienrf" %%% "play-json-derived-codecs" % "4.0.0",
    "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-M12"
  ))
  // "org.joda" % "joda-convert" % "1.8.2" maybe add this to jvm-side

}
