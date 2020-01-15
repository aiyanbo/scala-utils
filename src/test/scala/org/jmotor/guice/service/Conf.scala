package org.jmotor.guice.service

/**
 *
 * @author AI
 *         2020/1/15
 */
trait Conf

final case class StringConfig(value: String) extends Conf

final case class LongConfig(value: Long) extends Conf
