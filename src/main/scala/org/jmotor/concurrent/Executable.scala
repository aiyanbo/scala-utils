package org.jmotor.concurrent

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
trait Executable {

  def executeSafely[T](block: ⇒ Future[T], exceptionCaught: Throwable ⇒ Unit)(implicit ec: ExecutionContext): Unit = {
    try {
      block.recover {
        case NonFatal(t) ⇒ exceptionCaught(t)
      }
    } catch {
      case NonFatal(t) ⇒ exceptionCaught(t)
    }
  }

}

object Executable extends Executable
