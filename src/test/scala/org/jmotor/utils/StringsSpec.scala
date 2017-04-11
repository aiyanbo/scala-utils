package org.jmotor.utils

import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class StringsSpec extends FunSuite {

  test("Avoid blank") {
    assert(Strings.avoidBlank(Option("  ")).isEmpty)
  }

  test("Is empty") {
    assert(Strings.isEmpty(Option("   ")))
  }

  test("Non empty") {
    assert(Strings.nonEmpty(Option("   a ")))
  }

}
