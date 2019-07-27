package org.jmotor.grpc.logger

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.{ CallOptions, Channel, ClientCall, ClientInterceptor, ForwardingClientCallListener, Metadata, MethodDescriptor, Status }
import org.jmotor.grpc.MetadataKeys

/**
 *
 * @author AI
 *         2019-06-28
 */
class RequestLogInterceptor(requestId: Option[String], service: Option[String]) extends ClientInterceptor with LazyLogging {

  private[this] lazy val serviceName = service.getOrElse("-")

  override def interceptCall[ReqT, RespT](method: MethodDescriptor[ReqT, RespT], callOptions: CallOptions, next: Channel): ClientCall[ReqT, RespT] = {
    new SimpleForwardingClientCall[ReqT, RespT](next.newCall(method, callOptions)) {
      override def start(responseListener: ClientCall.Listener[RespT], headers: Metadata): Unit = {
        val started = System.nanoTime()
        val methodType = method.getType.name()
        val fullMethodName = method.getFullMethodName
        val id = requestId.getOrElse(UUID.randomUUID().getLeastSignificantBits.toHexString)
        val message = s"$id [$serviceName] $methodType $fullMethodName"
        logger.info(message)
        headers.put(MetadataKeys.REQUEST_ID_KEY, id)
        super.start(new ClientCallListener[RespT](responseListener, id, methodType, fullMethodName, started), headers)
      }
    }
  }

  private[this] class ClientCallListener[RespT](
      delegate:  ClientCall.Listener[RespT],
      requestId: String, methodType: String, methodName: String, started: Long)
    extends ForwardingClientCallListener.SimpleForwardingClientCallListener[RespT](delegate) {

    override def onClose(status: Status, trailers: Metadata): Unit = {
      val cost = (System.nanoTime() - started) / 1000000.0
      val message = s"$requestId [$serviceName] $methodType $methodName ${status.getCode.name()} cost: $cost"
      if (status.isOk) {
        logger.info(message)
      } else {
        logger.error(message)
      }
      super.onClose(status, trailers)
    }

  }

}

object RequestLogInterceptor {

  def apply(): RequestLogInterceptor = new RequestLogInterceptor(None, None)

  def apply(service: String): RequestLogInterceptor = new RequestLogInterceptor(None, Option(service))

  def ofRequestId(requestId: String): RequestLogInterceptor = new RequestLogInterceptor(Option(requestId), None)

  def ofRequestId(requestId: String, service: String): RequestLogInterceptor = new RequestLogInterceptor(Option(requestId), Option(service))

}
