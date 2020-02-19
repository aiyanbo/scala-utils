package org.jmotor.guice

import com.google.common.reflect.TypeToken
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder

import scala.jdk.CollectionConverters._
import scala.reflect._

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
abstract class AbstractMultiModuleSupport extends AbstractModuleSupport {

  def bindMultiComponent[T: ClassTag](packageNames: String*): Unit = {
    val supperClazz = classTag[T].runtimeClass
    val builder: Multibinder[T] = Multibinder.newSetBinder(binder(), supperClazz.asInstanceOf[Class[T]])
    val supper = TypeToken.of(supperClazz)
    bindClasses[T](supper.asInstanceOf[TypeToken[T]], builder, packageNames)
  }

  def bindMultiComponent[T](typeLiteral: TypeLiteral[T], packageNames: String*): Unit = {
    val builder: Multibinder[T] = Multibinder.newSetBinder(binder(), typeLiteral)
    val supper = TypeToken.of(typeLiteral.getRawType)
    bindClasses[T](supper.asInstanceOf[TypeToken[T]], builder, packageNames)
  }

  private[this] def bindClasses[T](supper: TypeToken[T], builder: Multibinder[T], packageNames: Seq[String]): Unit = {
    packageNames.foreach { packageName ⇒
      CLASS_PATH.getTopLevelClassesRecursive(packageName).asScala.map(_.load()).foreach { clazz ⇒
        if (TypeToken.of(clazz).isSubtypeOf(supper)) {
          builder.addBinding().to(clazz.asInstanceOf[Class[T]])
        }
      }
    }
  }

}
