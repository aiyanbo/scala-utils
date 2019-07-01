package org.jmotor.grpc

import java.util
import java.util.{ Locale, NoSuchElementException, Objects }

import com.google.protobuf.MessageOrBuilder
import com.typesafe.scalalogging.LazyLogging
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.jmotor.i18n.Messages
import org.jmotor.utils.Strings
import org.jmotor.validation.ValidationException

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import scala.util.{ Failure, Success }

/**
 *
 * @author AI
 *         2019-06-25
 */
trait GrpcServiceSupport extends LazyLogging {

  val errors: Messages = Messages("i18n/errors")

  def parseLanguage(languageOpt: Option[String]): Locale = {
    languageOpt.fold(Locale.getDefault) { lang ⇒
      val ranges = Locale.LanguageRange.parse(lang)
      Option(Locale.lookup(ranges, util.Arrays.asList(Locale.getAvailableLocales: _*))) match {
        case None         ⇒ Locale.getDefault
        case Some(locale) ⇒ locale
      }
    }
  }

  def handleRequest[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S])
    (handler: ⇒ Future[S])(implicit ec: ExecutionContext): Unit = {
    implicit val locale: Locale = Locale.getDefault
    handleUnary(request, observer, handler)
  }

  def handleLocaleRequest[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S])
    (handler: Locale ⇒ Future[S])(implicit ec: ExecutionContext): Unit = {
    implicit val locale: Locale = extractLocale(request)
    handleUnary(request, observer, handler(locale))
  }

  def handleStreamRequest[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S])(handler: ⇒ Future[Unit])
    (implicit ec: ExecutionContext): Unit = {
    implicit val locale: Locale = Locale.getDefault
    handleStream(request, observer, handler)
  }

  def handleLocaleStreamRequest[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S])(handler: Locale ⇒ Future[Unit])
    (implicit ec: ExecutionContext): Unit = {
    implicit val locale: Locale = extractLocale(request)
    handleStream(request, observer, handler(locale))
  }

  def handleException(observer: StreamObserver[_], t: Throwable)(implicit locale: Locale): Unit = {
    logger.error(t.getLocalizedMessage, t)
    val (status, description) = t match {
      case _: NoSuchElementException ⇒ Status.INVALID_ARGUMENT -> t.getLocalizedMessage
      case e: ValidationException    ⇒ Status.INVALID_ARGUMENT -> errors.format(e.constraint.template, s"[${e.constraint.args.mkString(",")}]")
      case _                         ⇒ Status.INTERNAL -> t.getLocalizedMessage
    }
    observer.onError(status.withDescription(description).asRuntimeException())
    observer.onCompleted()
  }

  private def extractLocale(request: MessageOrBuilder): Locale = {
    val field = request.getDescriptorForType.findFieldByName("language")
    val languageOpt = if (Objects.nonNull(field)) Strings.toOption(request.getField(field).asInstanceOf[String]) else None
    parseLanguage(languageOpt)
  }

  private def handleUnary[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S], fn: ⇒ Future[S])
    (implicit locale: Locale, ec: ExecutionContext): Unit = {
    try {
      fn.onComplete {
        case Success(reply) ⇒
          observer.onNext(reply)
          observer.onCompleted()
        case Failure(t) ⇒ handleException(observer, t)
      }
    } catch {
      case NonFatal(t) ⇒ handleException(observer, t)
    }
  }

  private def handleStream[S <: MessageOrBuilder](request: MessageOrBuilder, observer: StreamObserver[S], fn: ⇒ Future[Unit])
    (implicit locale: Locale, ec: ExecutionContext): Unit = {
    try {
      fn.onComplete {
        case Success(_) ⇒ observer.onCompleted()
        case Failure(t) ⇒ handleException(observer, t)
      }
    } catch {
      case NonFatal(t) ⇒ handleException(observer, t)
    }
  }

}
