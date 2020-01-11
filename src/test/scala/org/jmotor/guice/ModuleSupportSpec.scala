package org.jmotor.guice

import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import org.jmotor.guice.service.MultiPingService
import org.jmotor.guice.service.PingService
import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class ModuleSupportSpec extends FunSuite {

  test("Bind components") {
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindComponents("org.jmotor.guice.service.impl")
    })
    val service = injector.getInstance(classOf[PingService])
    assert(service.ping() == "pong")
  }

  test("Test bind extension") {
    val config = ConfigFactory.parseString("extension.enabled = true")
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindExtendableComponents("org.jmotor.guice.service.impl", config)
    })
    val service = injector.getInstance(classOf[PingService])
    assert(service.ping() == "extension pong")
  }

  test("Test bind s") {
    val injector = Guice.createInjector(new AbstractMultiModuleSupport {
      override def configure(): Unit = bindMultiComponent[PingService]("org.jmotor.guice.service.impls")
    })
    val pings = injector.getInstance(classOf[MultiPingService]).pings
    assert(pings.contains("v1"))
    assert(pings.contains("v2"))
  }

}
