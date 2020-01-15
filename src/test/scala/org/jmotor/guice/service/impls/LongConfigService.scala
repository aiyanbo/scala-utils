package org.jmotor.guice.service.impls

import org.jmotor.guice.service.ConfigService
import org.jmotor.guice.service.LongConfig

/**
 *
 * @author AI
 *         2020/1/15
 */
class LongConfigService extends ConfigService[LongConfig] {

  override def parse(value: String): LongConfig = LongConfig(value.toLong)

}
