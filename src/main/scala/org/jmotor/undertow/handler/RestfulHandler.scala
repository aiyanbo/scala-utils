package org.jmotor.undertow.handler

import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging
import io.undertow.security.api.AuthenticationMechanism.AuthenticationMechanismOutcome
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.util.Methods._
import io.undertow.util.StatusCodes
import org.jmotor.concurrent.Executable

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.runtime.BoxedUnit

/**
 * Component:
 * Description:
 * Date: 2018/8/20
 *
 * @author AI
 */
trait RestfulHandler extends HttpHandler with Executable with LazyLogging {

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
          case AuthenticationMechanismOutcome.AUTHENTICATED ⇒
            executeSafely({
              val future = exchange.getRequestMethod match {
                case GET    ⇒ get(exchange)
                case PUT    ⇒ put(exchange)
                case POST   ⇒ post(exchange)
                case PATCH  ⇒ patch(exchange)
                case DELETE ⇒ delete(exchange)
              }
              future.map {
                case () | _: BoxedUnit ⇒
                  exchange.setStatusCode(StatusCodes.NO_CONTENT)
                  exchange.endExchange()
                case result ⇒
                  val contentType = getResponseContentType(exchange)
                  exchange.getResponseHeaders.put(Headers.CONTENT_TYPE, contentType)
                  exchange.setStatusCode(getResponseStatusCode(exchange, result))
                  exchange.getResponseSender.send(ByteBuffer.wrap(writeAsBytes(result)))
              }
            }, exceptionCaught)
          case outcome: AuthenticationMechanismOutcome ⇒
            val (status, bytes) = authenticateFailureResponse(exchange, outcome)
            exchange.setStatusCode(status)
            if (bytes.nonEmpty) {
              val contentType = getResponseContentType(exchange)
              exchange.getResponseHeaders.put(Headers.CONTENT_TYPE, contentType)
              exchange.getResponseSender.send(ByteBuffer.wrap(bytes))
            }
            exchange.endExchange()
        }, exceptionCaught)
    })
  }

  def writeAsBytes(result: Any): Array[Byte]

  @inline def getResponseStatusCode(exchange: HttpServerExchange, result: Any): Int = getResponseStatusCode(exchange)

  @inline def getResponseContentType(exchange: HttpServerExchange): String = ContentTypes.APPLICATION_JSON

  def getResponseStatusCode(exchange: HttpServerExchange): Int = {
    exchange.getRequestMethod match {
      case GET    ⇒ StatusCodes.OK
      case PUT    ⇒ StatusCodes.OK
      case POST   ⇒ StatusCodes.CREATED
      case PATCH  ⇒ StatusCodes.NO_CONTENT
      case DELETE ⇒ StatusCodes.NO_CONTENT
    }
  }

  def authenticateFailureResponse(exchange: HttpServerExchange, outcome: AuthenticationMechanismOutcome): (Int, Array[Byte]) = {
    val status = outcome match {
      case AuthenticationMechanismOutcome.NOT_ATTEMPTED     ⇒ StatusCodes.FORBIDDEN
      case AuthenticationMechanismOutcome.NOT_AUTHENTICATED ⇒ StatusCodes.UNAUTHORIZED
      case AuthenticationMechanismOutcome.AUTHENTICATED     ⇒ throw new IllegalStateException("Request has authenticated")
    }
    status -> Array.emptyByteArray
  }

  def authenticate(exchange: HttpServerExchange): Future[AuthenticationMechanismOutcome] = {
    Future.successful(AuthenticationMechanismOutcome.AUTHENTICATED)
  }

  def get(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def put(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def patch(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

  def post(exchange: HttpServerExchange): Future[Any] = Future.failed(new UnsupportedOperationException)

  def delete(exchange: HttpServerExchange): Future[Unit] = Future.failed(new UnsupportedOperationException)

}
