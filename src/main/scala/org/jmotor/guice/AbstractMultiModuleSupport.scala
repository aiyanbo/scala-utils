package org.jmotor.guice

import com.google.common.reflect.TypeToken
import com.google.inject.multibindings.Multibinder

import scala.collection.JavaConverters._
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
    packageNames.foreach { packageName ⇒
      CLASS_PATH.getTopLevelClassesRecursive(packageName).asScala.map(_.load()).foreach { clazz ⇒
        if (TypeToken.of(clazz).isSubtypeOf(supper)) {
          builder.addBinding().to(clazz.asInstanceOf[Class[T]])
        }
      }
    }
  }

}
