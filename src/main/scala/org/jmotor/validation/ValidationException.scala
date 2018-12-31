package org.jmotor.validation

/**
 * Component:
 * Description:
 * Date: 2018/8/28
 *
 * @author AI
 */
final case class ValidationException(constraint: ConstraintViolation) extends RuntimeException(constraint.toString)
