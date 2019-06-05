package org.jmotor.undertow.handler

import java.nio.ByteBuffer
import java.util.concurrent.{ Executor, Executors }

import io.undertow.server.{ HttpHandler, HttpServerExchange }
import io.undertow.util.Methods._
import io.undertow.util.{ Headers, StatusCodes }
import org.apache.logging.log4j.scala.Logging
import org.jmotor.concurrent.Executable

import scala.concurrent.{ ExecutionContext, Future }
import scala.runtime.BoxedUnit

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
trait RestfulHandler extends HttpHandler with Executable with Logging {

  private[this] lazy val contentType = "application/json;charset=utf-8"
  private[this] implicit lazy val ec: ExecutionContext = ExecutionContext.fromExecutor(workers)
  protected lazy val workers: Executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())

  protected val exceptionHandler: ExceptionHandler

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    exchange.dispatch(workers, () ⇒ {
      lazy val exceptionCaught = (t: Throwable) ⇒ {
        exceptionHandler.handleException(exchange, t)
        exchange.endExchange()
        logger.error(t.getLocalizedMessage, t)
      }
      executeSafely(
        authenticate(exchange).map {
          case false ⇒
            val (status, bytes) = authenticateFailureResponse(exchange)
            exchange.setStatusCode(status)
            if (bytes.nonEmpty) {
              exchange.getResponseSender.send(ByteBuffer.wrap(bytes))
            }
            exchange.endExchange()
          case true ⇒
            executeSafely({
              val future = exchange.getRequestMethod match {
                case GET    ⇒ get(exchange)
                case PUT    ⇒ put(exchange)
                case POST   ⇒ post(exchange)
                case PATCH  ⇒ patch(exchange)
                case DELETE ⇒ delete(exchange)
              }
              future.map {
                case Unit | _: BoxedUnit ⇒
                  exchange.setStatusCode(StatusCodes.NO_CONTENT)
                  exchange.endExchange()
                case result ⇒
                  exchange.getResponseHeaders.put(Headers.CONTENT_TYPE, contentType)
                  exchange.setStatusCode(getResponseStatusCode(exchange))
                  exchange.getResponseSender.send(ByteBuffer.wrap(writeAsBytes(result)))
              }
            }, exceptionCaught)
        }, exceptionCaught)
    })
  }

  def writeAsBytes(result: Any): Array[Byte]

  def getResponseStatusCode(exchange: HttpServerExchange): Int = {
    exchange.getRequestMethod match {
      case GET    ⇒ StatusCodes.OK
      case PUT    ⇒ StatusCodes.OK
      case POST   ⇒ StatusCodes.CREATED
      case PATCH  ⇒ StatusCodes.NO_CONTENT
      case DELETE ⇒ StatusCodes.NO_CONTENT
    }
  }

  def authenticateFailureResponse(exchange: HttpServerExchange): (Int, Array[Byte]) = {
    StatusCodes.FORBIDDEN -> Array.emptyByteArray
  }

  def authenticate(exchange: HttpServerExchange): Future[Boolean] = Future.successful(true)

  def get(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def put(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def patch(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

  def post(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def delete(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

}
