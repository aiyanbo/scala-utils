package org.jmotor.utils

import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class ObjectsSpec extends FunSuite {

  test("Get properties") {
    val properties = Objects.properties(Foo("bar", "foo"))
    assert(properties.exists(e ⇒ e._1 == "bar" && e._2 == "bar"))
  }

  test("Ignore") {
    val properties = Objects.properties(Foo("bar", "foo"), "foo")
    assert(properties.keySet.contains("bar") && !properties.keySet.contains("foo"))
  }

}

final case class Foo(bar: String, foo: String)
