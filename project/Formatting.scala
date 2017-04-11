import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.{AutoPlugin, Def, PluginTrigger, Setting, _}

import scala.collection.immutable
import scalariform.formatter.preferences.FormattingPreferences

object Formatting extends AutoPlugin{

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = formatSettings

  lazy val formatSettings: immutable.Seq[Setting[_]] = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test := formattingPreferences
  )

  lazy val docFormatSettings: immutable.Seq[Setting[_]] = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := docFormattingPreferences,
    ScalariformKeys.preferences in Test := docFormattingPreferences
  )

  def formattingPreferences: FormattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }

  def docFormattingPreferences: FormattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}