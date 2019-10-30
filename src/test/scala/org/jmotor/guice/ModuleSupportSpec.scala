package org.jmotor.guice

import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
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

}
