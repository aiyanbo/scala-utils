package org.jmotor.config

import com.typesafe.config.ConfigFactory
import org.scalatest.FunSuite
import org.jmotor.config.ConfigConversions._

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class ConfigConversionsSpec extends FunSuite {

  private[this] final val config = ConfigFactory.parseString(
    s"""
       |name = "Andy Ai"
       |age = 18
     """.stripMargin
  )

  test("Get long configs") {
    val addressOpt = config.getStringOpt("address")
    assert(addressOpt.isEmpty)
    val ageOpt = config.getIntOpt("age")
    assert(ageOpt.contains(18))
  }

}
