package org.jmotor.grpc

import com.google.common.reflect.ClassPath
import com.google.common.reflect.TypeToken
import com.google.inject.Injector
import io.grpc.BindableService

import scala.jdk.CollectionConverters._

/**
 * Component:
 * Description:
 * Date: 2018/9/7
 *
 * @author AI
 */
object Services {

  @scala.deprecated("using org.jmotor.grpc.Services.loadGrpcServices", "1.0.15")
  def getGrpcServices(injector: Injector, packageName: String): Set[BindableService] = {
    loadGrpcServices(injector, packageName)
  }

  def loadGrpcServices(injector: Injector, packageName: String): Set[BindableService] = {
    loadGrpcServices(this.getClass.getClassLoader, injector, packageName)
  }

  def loadGrpcServices(loader: ClassLoader, injector: Injector, packageName: String): Set[BindableService] = {
    val classPath = ClassPath.from(loader)
    val serviceClazz: Class[BindableService] = classOf[BindableService]
    val classes = classPath.getTopLevelClassesRecursive(packageName).asScala
    classes.map(_.load()).filter { clazz ⇒
      val types = TypeToken.of(clazz).getTypes.asScala
      types.exists(_.getRawType == serviceClazz)
    }.map { clazz ⇒
      val constructor = clazz.getConstructors.head
      val parameters = constructor.getParameterTypes.map {
        case c if c == classOf[Injector] ⇒ injector
        case c                           ⇒ injector.getInstance(c).asInstanceOf[Object]
      }
      constructor.newInstance(parameters: _*).asInstanceOf[BindableService]
    }.toSet
  }

}
