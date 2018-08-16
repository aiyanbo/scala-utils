package org.jmotor.http.router

import org.jmotor.guice.service.PingService
import org.jmotor.http.RoutingHandler

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
class PingRouter(pingService: PingService) extends RoutingHandler {

  override def route: String = "/v3/services/ping"

  override def methods: Set[String] = single("GET")

}
