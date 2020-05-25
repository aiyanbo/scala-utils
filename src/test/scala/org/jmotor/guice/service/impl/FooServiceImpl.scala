package org.jmotor.guice.service.impl

import org.jmotor.guice.service.FooService

/**
 *
 * @author AI
 *         2020/5/25
 */
class FooServiceImpl extends FooService {

  override def run(): String = "run"

  override def call(): String = "call"

}
