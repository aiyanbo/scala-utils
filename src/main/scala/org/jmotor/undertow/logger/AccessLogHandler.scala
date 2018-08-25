package org.jmotor.undertow.logger

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.{ Objects, UUID }

import com.google.common.net.HttpHeaders
import io.undertow.server.{ ExchangeCompletionListener, HttpHandler, HttpServerExchange }
import io.undertow.util.HeaderValues
import org.apache.logging.log4j.{ LogManager, Logger }

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
class AccessLogHandler(next: HttpHandler, service: Option[String] = None) extends HttpHandler {

  private[this] val logger: Logger = LogManager.getLogger("access")
  private[this] val listener: ExchangeCompletionListener =
    (exchange: HttpServerExchange, nextListener: ExchangeCompletionListener.NextListener) ⇒ {
      try {
        val requestId = getRequestId(exchange)
        if (logger.isInfoEnabled) {
          logger.info(accessLogMessage(requestId, exchange))
        }
      } finally {
        nextListener.proceed()
      }
    }

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    exchange.addExchangeCompleteListener(listener)
    next.handleRequest(exchange)
  }

  private[this] def getRequestId(exchange: HttpServerExchange): String = {
    val requestId = exchange.getRequestHeaders.get("X-Request-Id")
    if (isEmpty(requestId)) {
      UUID.randomUUID().getLeastSignificantBits.toHexString
    } else {
      requestId.getFirst
    }
  }

  private[this] def accessLogMessage(requestId: String, exchange: HttpServerExchange): String = {
    val cost = (System.nanoTime() - exchange.getRequestStartTime) / 1000000.0
    val path = exchange.getRequestPath
    val status = exchange.getStatusCode
    val headers = exchange.getRequestHeaders
    val protocol = exchange.getProtocol.toString
    val bytesSent = exchange.getResponseBytesSent
    val method = exchange.getRequestMethod.toString
    val referer = headers.get(HttpHeaders.REFERER)
    val ua = headers.getFirst(HttpHeaders.USER_AGENT)
    val now = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val referred = if (isEmpty(referer)) "-" else referer.getFirst
    val sn = service.getOrElse("-")
    val address = exchange.getSourceAddress.getAddress.getHostAddress
    s"""$address $sn $requestId - [$now] "$method $path $protocol" $status $bytesSent $cost "$referred" "$ua""""
  }

  private[this] def isEmpty(values: HeaderValues): Boolean = Objects.isNull(values) || values.isEmpty

}
