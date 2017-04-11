package org.jmotor.config

import com.typesafe.config.Config

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._
import scala.language.implicitConversions

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
object ConfigConversions {

  implicit class ConfigWrapper(config: Config) {

    implicit def getIntOpt(path: String): Option[Int] = getOpt(config.getInt(path))

    implicit def getLongOpt(path: String): Option[Long] = getOpt(config.getLong(path))

    implicit def getStringOpt(path: String): Option[String] = getOpt(config.getString(path))

    implicit def getConfigOpt(path: String): Option[Config] = getOpt(config.getConfig(path))

    implicit def getStringSeqOpt(path: String): Option[Seq[String]] = getOpt(config.getStringList(path).asScala)

    implicit def getIntSeqOpt(path: String): Option[Seq[Int]] = getOpt(config.getIntList(path).asScala.map(_.asInstanceOf[Int]))

    private[this] def getOpt[T](f: ⇒ T): Option[T] = {
      Try(f) match {
        case Success(value) ⇒ Some(value)
        case Failure(_)     ⇒ None
      }
    }
  }

}
