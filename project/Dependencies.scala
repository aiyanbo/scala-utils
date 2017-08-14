import sbt.Keys.libraryDependencies
import sbt.{Def, _}

object Dependencies extends AutoPlugin {


  override def trigger: PluginTrigger = allRequirements


  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies := dependencies)

  object Versions {
    val guava = "23.0"
    val guice = "4.1.0"
    val config = "1.3.1"
    val scala212 = "2.12.3"
    val scalaTest = "3.0.3"
    val scala211 = "2.11.11"
  }

  object Compile {
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guava: ModuleID = "com.google.guava" % "guava" % Versions.guava
    val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice exclude("com.google.guava", "guava")
  }

  object Test {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  }

  import Compile._

  lazy val dependencies: Seq[ModuleID] = Seq(config, guava, guice).map(_ % "provided") :+ Test.scalaTest

}