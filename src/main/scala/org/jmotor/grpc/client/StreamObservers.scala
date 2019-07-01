package org.jmotor.grpc.client

import io.grpc.stub.StreamObserver

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ Future, Promise }

/**
 *
 * @author AI
 *         2019-07-01
 */
object StreamObservers {

  def unary[T]: SimpleObserver[T] = new SimpleObserver[T]()

  def stream[T]: CollectionObserver[T] = new CollectionObserver[T]()

}

class SimpleObserver[T] extends StreamObserver[T] {
  private[this] var reply: T = _
  private[this] val promise = Promise[T]

  override def onNext(value: T): Unit = reply = value

  override def onError(t: Throwable): Unit = promise.failure(t)

  override def onCompleted(): Unit = promise.success(reply)

  def future: Future[T] = promise.future

}

class CollectionObserver[T] extends StreamObserver[T] {
  private[this] val promise = Promise[Seq[T]]
  private[this] var replies: ListBuffer[T] = ListBuffer[T]()

  override def onNext(value: T): Unit = replies += value

  override def onError(t: Throwable): Unit = promise.failure(t)

  override def onCompleted(): Unit = promise.success(replies.toSeq)

  def future: Future[Seq[T]] = promise.future

}
