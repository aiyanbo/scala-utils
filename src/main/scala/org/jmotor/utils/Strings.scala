package org.jmotor.utils

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
object Strings {

  def toOption(str: String): Option[String] = {
    if (java.util.Objects.isNull(str) || str.trim.isEmpty) None else Option(str)
  }

  def avoidBlank(stringOpt: Option[String]): Option[String] = {
    stringOpt.flatMap(s ⇒ if (s.trim.isEmpty) None else stringOpt)
  }

  def nonEmpty(stringOpt: Option[String]): Boolean = !isEmpty(stringOpt)

  def isEmpty(stringOpt: Option[String]): Boolean = avoidBlank(stringOpt).isEmpty

}
