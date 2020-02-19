package org.jmotor.guice.service

import javax.inject.Inject

import scala.jdk.CollectionConverters._

/**
 *
 * @author AI
 *         2020/1/11
 */
class MultiPingService @Inject() (services: java.util.Set[PingService]) {

  def pings: Set[String] = services.asScala.map(_.ping()).toSet

}
