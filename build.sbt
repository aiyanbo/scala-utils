import ReleaseTransformations._

name := "scala-utils"

organization := "org.jmotor"

enablePlugins(Dependencies, Publishing)

scalaVersion := Dependencies.Versions.scala212

dependencyUpgradeModuleNames := Map(
  "log4j-.*" -> "log4j",
  "scala-library" -> "scala",
  "undertow-.*" -> "undertow"
)

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

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
