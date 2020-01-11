package org.jmotor.guice.service.impls

import org.jmotor.guice.service.PingService

/**
 *
 * @author AI
 *         2020/1/11
 */
class PingServiceImplV2 extends PingService {

  override def ping(): String = "v2"

}
