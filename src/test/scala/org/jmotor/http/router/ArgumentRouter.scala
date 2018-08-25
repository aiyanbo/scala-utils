package org.jmotor.http.router

import org.jmotor.http.RoutingHandler

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
class ArgumentRouter extends RoutingHandler {

  override def route: String = "/users/{id}"

  override def methods: Set[String] = multi("GET", "POST")

}
