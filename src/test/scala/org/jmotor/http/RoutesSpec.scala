package org.jmotor.http

import com.google.inject.Guice
import org.jmotor.guice.AbstractModuleSupport
import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
class RoutesSpec extends FunSuite {

  test("load handlers") {
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindComponents("org.jmotor.guice.service.impl")
    })
    val routes = Routes.getRoutingHandlers(injector, "org.jmotor.http.router")
    assert(routes.exists(h ⇒ h.route == "/v3/services/ping"))
  }

}
