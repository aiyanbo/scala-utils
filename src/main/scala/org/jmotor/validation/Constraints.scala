package org.jmotor.validation

import org.jmotor.utils.Strings

import scala.util.control.NonFatal

/**
 * Component:
 * Description:
 * Date: 2018/8/29
 *
 * @author AI
 */
object Constraints {

  val Enums = "enums"

  val NotBlank = "not_blank"

  val NotEmpty = "not_empty"

  val MinLength = "min_length"

  val MaxLength = "max_length"

  def notBlank(field: String, value: String): Unit = {
    check(ConstraintViolation(field, NotBlank)) {
      assert(Strings.toOption(value).isDefined)
    }
  }

  def notEmpty(field: String, value: String): Unit = {
    check(ConstraintViolation(field, NotEmpty)) {
      assert(Strings.toOption(value).isDefined)
    }
  }

  def notEmpty(field: String, values: Traversable[_]): Unit = {
    check(ConstraintViolation(field, NotEmpty)) {
      assert(values.nonEmpty)
    }
  }

  def matchValues(field: String, values: Seq[String], matches: Seq[String]): Unit = {
    check(ConstraintViolation(field, Enums, matches: _*)) {
      values.foreach { value ⇒
        assert(matches.contains(value))
      }
    }
  }

  def minLength(field: String, value: String, length: Int): Unit = {
    check(ConstraintViolation(field, MinLength)) {
      assert(value.length >= length)
    }
  }

  def minLength(field: String, values: Traversable[_], length: Int): Unit = {
    check(ConstraintViolation(field, MinLength)) {
      assert(values.size >= length)
    }
  }

  def maxLength(field: String, value: String, length: Int): Unit = {
    check(ConstraintViolation(field, MaxLength)) {
      assert(value.length <= length)
    }
  }

  def maxLength(field: String, values: Traversable[_], length: Int): Unit = {
    check(ConstraintViolation(field, MaxLength)) {
      assert(values.size <= length)
    }
  }

  def check(constraint: ⇒ ConstraintViolation)(c: ⇒ Unit): Unit = {
    try {
      c
    } catch {
      case NonFatal(_) ⇒ throw ValidationException(constraint)
    }
  }

}
