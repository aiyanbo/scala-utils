package org.jmotor.grpc.logger

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.{ CallOptions, Channel, ClientCall, ClientInterceptor, Metadata, MethodDescriptor }
import org.jmotor.grpc.MetadataKeys

/**
 *
 * @author AI
 *         2019-06-28
 */
class RequestLogInterceptor(service: Option[String]) extends ClientInterceptor with LazyLogging {

  private[this] lazy val serviceName = service.getOrElse("-")

  override def interceptCall[ReqT, RespT](method: MethodDescriptor[ReqT, RespT], callOptions: CallOptions, next: Channel): ClientCall[ReqT, RespT] = {
    new SimpleForwardingClientCall[ReqT, RespT](next.newCall(method, callOptions)) {
      override def start(responseListener: ClientCall.Listener[RespT], headers: Metadata): Unit = {
        val methodType = method.getType.name()
        val fullMethodName = method.getFullMethodName
        val requestId = UUID.randomUUID().getLeastSignificantBits.toHexString
        val message = s"$requestId [$serviceName] $methodType $fullMethodName"
        logger.info(message)
        headers.put(MetadataKeys.REQUEST_ID_KEY, requestId)
        super.start(responseListener, headers)
      }
    }
  }

}

object RequestLogInterceptor {

  def apply(): RequestLogInterceptor = new RequestLogInterceptor(None)

  def apply(service: String): RequestLogInterceptor = new RequestLogInterceptor(Option(service))

}
