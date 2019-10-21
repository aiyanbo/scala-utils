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
