package org.jmotor.config

import com.typesafe.config.ConfigFactory
import org.jmotor.config.ConfigConversions._
import org.scalatest.funsuite.AnyFunSuite

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class ConfigConversionsSpec extends AnyFunSuite {

  private[this] final val config = ConfigFactory.parseString(
    s"""
       |name = "Andy Ai"
       |age = 18
       |valid = true
       |nums = [1,2,3,4]
       |
       |clients {
       |  timeout = 1
       |  http {
       |    address = "0.0.0.0"
       |  }
       |
       |  grpc {
       |    address = "0.0.0.0"
       |  }
       |}
     """.stripMargin)

  test("Get long configs") {
    val addressOpt = config.getStringOpt("address")
    assert(addressOpt.isEmpty)
    val ageOpt = config.getIntOpt("age")
    assert(ageOpt.contains(18))
  }

  test("Get boolean opt") {
    val boolOpt = config.getBooleanOpt("invalid")
    assert(boolOpt.isEmpty)
    assert(config.getBooleanOpt("valid").contains(true))
  }

  test("Get int seq opt") {
    assert(config.getIntSeqOpt("nums2").isEmpty)
    assert(config.getIntSeqOpt("nums").exists(_.forall(_ < 5)))
  }

  test("Get sub config names") {
    val names = config.getSubConfigNames("clients")
    assert(names.size == 2)
    Seq("http", "grpc").foreach { name ⇒
      assert(names.contains(name))
    }
  }

}
