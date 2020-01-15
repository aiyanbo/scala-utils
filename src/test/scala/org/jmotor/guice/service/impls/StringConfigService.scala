package org.jmotor.guice.service.impls

import org.jmotor.guice.service.ConfigService
import org.jmotor.guice.service.StringConfig

/**
 *
 * @author AI
 *         2020/1/15
 */
class StringConfigService extends ConfigService[StringConfig] {

  override def parse(value: String): StringConfig = StringConfig(value)

}
