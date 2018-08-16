package org.jmotor.http

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
trait RoutingHandler {

  def route: String

  def methods: Set[String]

  def pattern: Option[String] = None

  def single(method: String): Set[String] = Set(method)

  def multi(methods: String*): Set[String] = methods.toSet

}
