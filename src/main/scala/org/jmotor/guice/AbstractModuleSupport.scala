package org.jmotor.guice

import java.util.Objects

import com.google.common.reflect.ClassPath
import com.google.common.reflect.ClassPath.ClassInfo
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.jmotor.config.ConfigConversions._

import scala.jdk.CollectionConverters._
import scala.collection.mutable

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
abstract class AbstractModuleSupport extends AbstractModule with LazyLogging {

  final val CLASS_PATH: ClassPath = ClassPath.from(this.getClass.getClassLoader)

  def bindComponents(packageName: String): Unit = {
    CLASS_PATH.getTopLevelClasses(packageName).asScala.foreach(bindComponent)
  }

  def bindExtendableComponents(packageName: String, config: Config): Unit = {
    val extensionPackage = packageName.substring(0, packageName.lastIndexOf('.')) + ".extension"
    val enabled: Boolean = config.getBooleanOpt("extension.enabled").getOrElse(false)
    bindExtendableComponents(packageName, extensionPackage, enabled)
  }

  def bindExtendableComponents(packageName: String, extensionPackage: String, config: Config): Unit = {
    val enabled: Boolean = config.getBooleanOpt("extension.enabled").getOrElse(false)
    bindExtendableComponents(packageName, extensionPackage, enabled)
  }

  def bindExtendableComponents(packageName: String, extensionPackage: String, enabled: Boolean): Unit = {
    val serviceClasses = mutable.Map[String, ClassInfo]()
    val packages = if (enabled) {
      logger.info(s"Load extension package: $extensionPackage")
      Seq(packageName, extensionPackage)
    } else {
      Seq(packageName)
    }
    packages.foreach { packageName ⇒
      CLASS_PATH.getTopLevelClasses(packageName).asScala.foreach { classInfo ⇒
        serviceClasses += classInfo.getSimpleName -> classInfo
      }
    }
    serviceClasses.values.foreach(bindComponent)
  }

  def loadClasses(packageName: String): Set[Class[_]] = {
    CLASS_PATH.getTopLevelClasses(packageName).asScala.map(_.load()).toSet
  }

  def bindComponent(classInfo: ClassPath.ClassInfo): Unit = {
    val clazz = classInfo.load()
    val interfaces = clazz.getInterfaces
    val interfaceOpt = interfaces.find { irfe ⇒
      clazz.getSimpleName.contains(irfe.getSimpleName) && clazz.getPackage.getName.contains(irfe.getPackage.getName)
    }
    if (Objects.nonNull(interfaces) && interfaces.nonEmpty && interfaceOpt.isDefined) {
      bind(interfaceOpt.get.asInstanceOf[Class[Any]]).to(clazz.asInstanceOf[Class[Any]])
    }
  }

}
