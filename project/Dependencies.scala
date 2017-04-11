import sbt.Keys.libraryDependencies
import sbt.{Def, _}

object Dependencies extends AutoPlugin{


  override def trigger: PluginTrigger = allRequirements


  override def projectSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies := dependencies)

  object Versions {
    val guava = "21.0"
    val guice = "4.1.0"
    val config = "1.3.1"
    val scala211 = "2.11.8"
    val scala212 = "2.12.1"
    val scalaTest = "3.0.1"
    val scalaLogging = "3.5.0"
  }

  object Compile {
    val config: ModuleID = "com.typesafe" % "config" % Versions.config
    val guava: ModuleID = "com.google.guava" % "guava" % Versions.guava
    val scalaLogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
    val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice % "provided" exclude("com.google.guava", "guava")
  }

  object Test {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  }

  import Compile._

  lazy val dependencies = Seq(config, guava,guice, scalaLogging, Test.scalaTest)

}