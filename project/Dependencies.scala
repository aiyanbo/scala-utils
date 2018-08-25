import sbt.Keys.libraryDependencies
import sbt.{ Def, _ }

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= dependencies)

  object Versions {
    val guava = "23.0"
    val guice = "4.2.0"
    val config = "1.3.3"
    val log4j2 = "2.11.1"
    val scalatest = "3.0.5"
    val scala212 = "2.12.6"
    val scalikeJdbc = "3.2.3"
    val undertow = "2.0.6.Final"
  }

  object Compiles {
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice
    val undertow: ModuleID = "io.undertow" % "undertow-core" % Versions.undertow
    val log4j2: Seq[ModuleID] = Seq(
      "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
      "org.apache.logging.log4j" % "log4j-api" % Versions.log4j2)
    val scalikeJdbc: ModuleID = "org.scalikejdbc" %% "scalikejdbc" % Versions.scalikeJdbc
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = (log4j2 ++ Seq(
    config, guice, undertow, scalikeJdbc)).map(_ % Provided) :+ Tests.scalaTest

}
