package org.jmotor.utils

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
object Objects {

  def properties(obj: Any, excludes: String*): Map[String, Any] = {
    val fields = obj.getClass.getDeclaredFields.map { field ⇒
      field.setAccessible(true)
      field.getName → field.get(obj)
    }.toMap
    if (excludes.nonEmpty) {
      fields.filterNot(entry ⇒ excludes.contains(entry._1))
    } else {
      fields
    }
  }

}
