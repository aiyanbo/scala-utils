package org.jmotor.guice.service

/**
 *
 * @author AI
 *         2020/1/15
 */
trait ConfigService[T <: Conf] {

  def parse(value: String): T

}
