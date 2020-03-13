package org.jmotor.config

import com.typesafe.config.ConfigFactory
import org.jmotor.config.ConfigConversions._
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.duration._

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
       |ints = [1,2,3,4]
       |longs = [1,2,3,4]
       |doubles = [0.1,0.2,0.3,0.4]
       |strings = ["a","b"]
       |timeout = 1 seconds
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
       |
       |module.enabled += "a"
       |module.enabled += "b"
       |module.enabled += "c"
       |module.disabled += "a"
       |project.enabled += "1"
     """.stripMargin).resolve()

  test("Get value opt") {
    val addressOpt = config.getStringOpt("address")
    assert(addressOpt.isEmpty)
    val ageOpt = config.getIntOpt("age")
    assert(ageOpt.contains(18))
    assert(config.getLongOpt("age1").isEmpty)
    assert(config.getDoubleOpt("age2").isEmpty)
    assert(config.getDurationOpt("timeout1").isEmpty)
    assert(config.getDurationOpt("timeout").contains(1.seconds))
  }

  test("Get boolean opt") {
    val boolOpt = config.getBooleanOpt("invalid")
    assert(boolOpt.isEmpty)
    assert(config.getBooleanOpt("valid").contains(true))
  }

  test("Get value seq opt") {
    assert(config.getIntSeqOpt("ints2").isEmpty)
    assert(config.getIntSeqOpt("ints").exists(_.forall(_ < 5)))
    assert(config.getLongSeqOpt("longs2").isEmpty)
    assert(config.getLongSeqOpt("longs").exists(_.forall(_ < 5)))
    assert(config.getDoubleSeqOpt("doubles2").isEmpty)
    assert(config.getDoubleSeqOpt("doubles").exists(_.forall(_ < 0.5)))
    assert(config.getStringSeqOpt("strings2").isEmpty)
    config.getStringSeqOpt("strings").get.foreach { value ⇒
      assert(Set("a", "b").contains(value))
    }
  }

  test("Get sub config names") {
    val names = config.getSubConfigNames("clients")
    assert(names.size == 2)
    Seq("http", "grpc").foreach { name ⇒
      assert(names.contains(name))
    }
  }

  test("Get enables") {
    val enables = config.getEnabledValues("module.enabled", "module.disabled")
    Seq("b", "c").foreach { v ⇒
      assert(enables.contains(v))
    }
    assert(!enables.contains("a"))
    val projects = config.getEnabledValues("project.enabled", "project.disabled")
    assert(projects.size == 1)
    assert(projects.head == "1")
    val nodes = config.getEnabledValues("nodes", "node.disabled")
    assert(nodes.isEmpty)
  }

}
