import Dependencies.Versions
import org.jmotor.sbt.plugin.ComponentSorter
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := "scala-utils"

organization := "org.jmotor"

enablePlugins(Dependencies, Publishing)

scalaVersion := Versions.scala

crossScalaVersions := Seq(Versions.scala212, Versions.scala)

dependencyUpgradeModuleNames := Map(
  "scala-library" -> "scala",
  "undertow-.*" -> "undertow"
)

dependencyUpgradeComponentSorter := ComponentSorter.ByAlphabetically

lazy val utf8: String = "UTF-8"
lazy val javaVersion: String = "1.8"

Compile / compile / javacOptions ++= Seq(
  "-source", javaVersion, "-target", javaVersion, "-encoding", utf8, "-deprecation"
)

Compile / doc / javacOptions ++= Seq(
  "-linksource", "-source", javaVersion, "-docencoding", utf8, "-charset", utf8, "-encoding", utf8, "-nodeprecated"
)

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
