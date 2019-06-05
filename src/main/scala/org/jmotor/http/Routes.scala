package org.jmotor.http

import java.nio.file.Paths

import com.google.common.reflect.{ ClassPath, TypeToken }
import com.google.inject.Injector

import scala.collection.JavaConverters._

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
object Routes {

  def getRoutingHandlers(injector: Injector, packageName: String): Set[RoutingHandler] = {
    val classPath = ClassPath.from(this.getClass.getClassLoader)
    val routingHandlerClazz: Class[RoutingHandler] = classOf[RoutingHandler]
    val classes = classPath.getTopLevelClassesRecursive(packageName).asScala
    classes.map(_.load()).filter { clazz ⇒
      val types = TypeToken.of(clazz).getTypes.asScala
      types.exists(_.getRawType == routingHandlerClazz)
    }.map { clazz ⇒
      val constructor = clazz.getConstructors.head
      val parameters = constructor.getParameterTypes.map {
        case c if c == classOf[Injector] ⇒ injector
        case c                           ⇒ injector.getInstance(c).asInstanceOf[Object]
      }
      constructor.newInstance(parameters: _*).asInstanceOf[RoutingHandler]
    }.toSet
  }

  def getRegexRoutes(handlers: Set[RoutingHandler], versioning: Option[String] = None): Set[String] = {
    handlers.map { routing ⇒
      val pattern = routing.pattern.getOrElse(routing.route)
      val router = versioning.fold(pattern)(version ⇒ Paths.get("/", version, pattern).toString)
      router.replaceAll("\\{\\w+\\}", """([\\\\w|-]+)""")
    }
  }

}
