import sbt.Keys.libraryDependencies
import sbt.{ Def, _ }

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= dependencies)

  object Versions {
    val guava = "23.0"
    val guice = "4.2.0"
    val config = "1.3.3"
    val scalatest = "3.0.5"
    val scala212 = "2.12.6"
    val scala211 = "2.11.11"
    val scalikeJdbc = "3.2.3"
  }

  object Compiles {
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice
    val scalikeJdbc: ModuleID = "org.scalikejdbc" %% "scalikejdbc" % Versions.scalikeJdbc
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(config, guice, scalikeJdbc).map(_ % Provided) :+ Tests.scalaTest

}
