package org.jmotor.guice.service.extension

import org.jmotor.guice.service.FooService

/**
 *
 * @author AI
 *         2020/5/25
 */
class FooServiceImpl extends org.jmotor.guice.service.impl.FooServiceImpl with FooService {

  override def call(): String = "invoke"

}
