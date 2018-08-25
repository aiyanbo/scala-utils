package org.jmotor.http.router

import org.jmotor.http.RoutingHandler

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
class PatternRouter extends RoutingHandler {

  override def route: String = "/permissions/{permission}"

  override def pattern: Option[String] = Option("/permissions/.*")

  override def methods: Set[String] = single("GET")

}
