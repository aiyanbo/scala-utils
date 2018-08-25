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
trait RestfulHandler extends HttpHandler with Executable with Logging { self ⇒

  private[this] lazy val contentType = "application/json;charset=utf-8"
  private[this] implicit lazy val ec: ExecutionContext = ExecutionContext.fromExecutor(workers)
  protected lazy val workers: Executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    exchange.dispatch(workers, () ⇒ {
      lazy val exceptionCaught = (t: Throwable) ⇒ {
        exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR)
        exchange.endExchange()
        logger.error(t.getLocalizedMessage, t)
      }
      executeSafely(
        authenticate(exchange).map {
          case false ⇒
            exchange.setStatusCode(StatusCodes.UNAUTHORIZED)
            exchange.endExchange()
          case true ⇒
            executeSafely({
              val (status, future) = exchange.getRequestMethod match {
                case GET    ⇒ StatusCodes.OK -> get(exchange)
                case PUT    ⇒ StatusCodes.OK -> put(exchange)
                case POST   ⇒ StatusCodes.CREATED -> post(exchange)
                case PATCH  ⇒ StatusCodes.NO_CONTENT -> patch(exchange)
                case DELETE ⇒ StatusCodes.NO_CONTENT -> delete(exchange)
              }
              future.map {
                case Unit | _: BoxedUnit ⇒
                  exchange.setStatusCode(StatusCodes.NO_CONTENT)
                  exchange.endExchange()
                case result ⇒
                  exchange.getResponseHeaders.put(Headers.CONTENT_TYPE, contentType)
                  exchange.setStatusCode(status)
                  exchange.getResponseSender.send(ByteBuffer.wrap(writeAsBytes(result)))
              }
            }, exceptionCaught)
        }, exceptionCaught)
    })
  }

  def writeAsBytes(result: Any): Array[Byte]

  def authenticate(exchange: HttpServerExchange): Future[Boolean] = Future.successful(true)

  def get(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def put(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def patch(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

  def post(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def delete(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

}
