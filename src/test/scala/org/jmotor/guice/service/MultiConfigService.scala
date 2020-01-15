package org.jmotor.guice.service

import javax.inject.Inject
import javax.inject.Singleton

import scala.collection.JavaConverters._

/**
 *
 * @author AI
 *         2020/1/15
 */
@Singleton
class MultiConfigService @Inject() (services: java.util.Set[ConfigService[Conf]]) {

  def impls: Seq[ConfigService[Conf]] = services.asScala.toSeq.map(_.asInstanceOf[ConfigService[Conf]])

}
