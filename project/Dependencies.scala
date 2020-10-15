import sbt.Def
import sbt.Keys.libraryDependencies
import sbt._

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= dependencies)

  object Versions {
    val config = "1.4.0"
    val grpcCore = "1.32.2"
    val grpcStub = "1.32.2"
    val guava = "23.0"
    val guice = "4.2.3"
    val guiceMultibindings = "4.2.3"
    val protobufJava = "3.13.0"
    val scala = "2.13.3"
    val scala212 = "2.12.10"
    val scalaI18n = "1.0.8"
    val scalaLogging = "3.9.2"
    val scalatest = "3.2.2"
    val scalikejdbc = "3.5.0"
    val undertow = "2.2.2.Final"
  }

  object Compiles {
    val protobuf: ModuleID = "com.google.protobuf" % "protobuf-java" % Versions.protobufJava
    val grpc: Seq[ModuleID] = Seq(
      "io.grpc" % "grpc-core" % Versions.grpcCore,
      "io.grpc" % "grpc-stub" % Versions.grpcCore)
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guice: Seq[ModuleID] = Seq(
      "com.google.inject" % "guice" % Versions.guice,
      "com.google.inject.extensions" % "guice-multibindings" % Versions.guice)
    val i18n: ModuleID = "org.jmotor" %% "scala-i18n" % Versions.scalaI18n
    val undertow: ModuleID = "io.undertow" % "undertow-core" % Versions.undertow
    val scalikeJdbc: ModuleID = "org.scalikejdbc" %% "scalikejdbc" % Versions.scalikejdbc
    val logging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = (grpc ++ guice ++ Seq(
    logging, config, i18n, protobuf, undertow, scalikeJdbc)).map(_ % Provided) :+ Tests.scalaTest

}
