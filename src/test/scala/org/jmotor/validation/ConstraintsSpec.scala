package org.jmotor.validation

import org.scalatest.{ FlatSpec, Matchers }

import scala.util.Try

/**
 *
 * @author AI
 *         2019-03-11
 */
class ConstraintsSpec extends FlatSpec with Matchers {

  "notBlank" should "throw exception when value is blank" in {
    val result = Try(Constraints.notBlank("name", ""))
    result.isFailure shouldBe true
  }

  "notBlank" should "check ok when value is not blank" in {
    val result = Try(Constraints.notBlank("name", "Andy"))
    result.isSuccess shouldBe true
  }

  "notEmpty" should "check ok and bad" in {
    Try(Constraints.notEmpty("ids", Seq.empty)).isFailure shouldBe true
    Try(Constraints.notEmpty("ids", Seq("1"))).isSuccess shouldBe true
  }

  "matchValues" should "check ok and bad" in {
    Try(Constraints.matchValues("platforms", Seq("abc"), Seq("web"))).isFailure shouldBe true
    Try(Constraints.matchValues("platforms", Seq("web"), Seq("web"))).isSuccess shouldBe true
  }

  "maxLength" should "check ok and bad" in {
    Try(Constraints.maxLength("name", "JJJJJJJJJJJJ", 3)).isFailure shouldBe true
    Try(Constraints.maxLength("name", Seq(1, 2, 3, 1), 3)).isFailure shouldBe true
    Try(Constraints.maxLength("name", "jj", 3)).isSuccess shouldBe true
    Try(Constraints.maxLength("name", Seq(1, 2, 3), 3)).isSuccess shouldBe true
  }

  "minLength" should "check ok and bad" in {
    Try(Constraints.minLength("name", "jj", 3)).isFailure shouldBe true
    Try(Constraints.minLength("name", Seq(1, 2), 3)).isFailure shouldBe true
    Try(Constraints.minLength("name", "jjj", 3)).isSuccess shouldBe true
    Try(Constraints.minLength("name", Seq(1, 2, 3), 3)).isSuccess shouldBe true
  }

}
