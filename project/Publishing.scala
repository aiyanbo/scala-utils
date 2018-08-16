import com.typesafe.sbt.SbtPgp.autoImportImpl.useGpg
import sbt.Keys._
import sbt.{ AutoPlugin, Credentials, Path, PluginTrigger, _ }

object Publishing extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(
    useGpg := false,
    publishMavenStyle := true,
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    },
    pomExtra :=
      <url>https://github.com/aiyanbo/scala-utils</url>
      <licenses>
        <license>
          <name>Apache License</name>
          <url>http://www.apache.org/licenses/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:aiyanbo/scala-utils.git</url>
        <connection>scm:git:git@github.com:aiyanbo/scala-utils.git</connection>
      </scm>
      <developers>
        <developer>
          <id>yanbo.ai</id>
          <name>Andy Ai</name>
          <url>http://aiyanbo.github.io/</url>
        </developer>
      </developers>)
}
