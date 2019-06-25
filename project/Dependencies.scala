import sbt.Keys.libraryDependencies
import sbt.{ Def, _ }

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= dependencies)

  object Versions {
    val config = "1.3.4"
    val grpcCore = "1.21.0"
    val guava = "23.0"
    val guice = "4.2.2"
    val scala212 = "2.12.8"
    val scala213 = "2.13.0"
    val scalaLogging = "3.9.2"
    val scalatest = "3.0.8"
    val scalikejdbc = "3.3.5"
    val undertow = "2.0.22.Final"
  }

  object Compiles {
    val grpc: ModuleID = "io.grpc" % "grpc-core" % Versions.grpcCore
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice
    val undertow: ModuleID = "io.undertow" % "undertow-core" % Versions.undertow
    val scalikeJdbc: ModuleID = "org.scalikejdbc" %% "scalikejdbc" % Versions.scalikejdbc
    val logging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(
    logging, config, guice, grpc, undertow, scalikeJdbc).map(_ % Provided) :+ Tests.scalaTest

}
