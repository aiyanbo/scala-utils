package org.jmotor.guice

import com.google.inject.{Guice, Injector}
import org.jmotor.guice.service.Service
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
    val service = injector.getInstance(classOf[Service])
    assert(service.ping() == "pong")
  }

}
