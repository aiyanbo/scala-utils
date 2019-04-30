package org.jmotor.undertow.handler

import io.undertow.server.HttpServerExchange

/**
 *
 * @author AI
 *         2019-03-18
 */
trait ExceptionHandler {

  def handleException(exchange: HttpServerExchange, cause: Throwable): Unit

}
