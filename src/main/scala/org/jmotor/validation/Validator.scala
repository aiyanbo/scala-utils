package org.jmotor.validation

/**
 * Component:
 * Description:
 * Date: 2018/8/28
 *
 * @author AI
 */
trait Validator[T] {

  def validate(value: T): Unit

}
