package org.jmotor.http

import com.google.inject.Guice
import org.jmotor.guice.AbstractModuleSupport
import org.jmotor.http.router.{ ArgumentRouter, PatternRouter, PingRouter }
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
    val handlers = Routes.getRoutingHandlers(injector, "org.jmotor.http.router")
    assert(handlers.exists(_.isInstanceOf[PingRouter]))
    assert(handlers.exists(_.isInstanceOf[PatternRouter]))
    assert(handlers.exists(_.isInstanceOf[ArgumentRouter]))
  }

  test("load routes") {
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindComponents("org.jmotor.guice.service.impl")
    })
    val handlers = Routes.getRoutingHandlers(injector, "org.jmotor.http.router")
    val routes = Routes.getRegexRoutes(handlers)

    assert(routes.contains("""/users/([\\w|-]+)"""))
    assert(routes.contains("""/permissions/.*"""))

  }

}
