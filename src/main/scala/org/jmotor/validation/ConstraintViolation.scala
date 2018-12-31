package org.jmotor.validation

/**
 * Component:
 * Description:
 * Date: 2018/8/28
 *
 * @author AI
 */
final case class ConstraintViolation(field: String, template: String, args: Any*)
