package org.jmotor.guice.service.extension

import org.jmotor.guice.service.PingService

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class PingServiceImpl extends PingService {

  override def ping(): String = "extension pong"

}
