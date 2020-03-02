package org.jmotor.grpc.logger

import java.net.InetSocketAddress
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.typesafe.scalalogging.Logger
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.inprocess.InProcessSocketAddress
import io.grpc.internal.GrpcUtil
import io.grpc.{ Grpc, Metadata, ServerCall, ServerCallHandler, ServerInterceptor, Status }
import org.jmotor.grpc.MetadataKeys

/**
 *
 * @author AI
 *         2019-06-28
 */
class AccessLogInterceptor(service: Option[String]) extends ServerInterceptor {
  private[this] final val NONE_INFO = "-"
  private[this] final val logger = Logger("access")
  private[this] lazy val serviceName = service.getOrElse(NONE_INFO)

  override def interceptCall[ReqT, RespT](call: ServerCall[ReqT, RespT], headers: Metadata,
                                          next: ServerCallHandler[ReqT, RespT]): ServerCall.Listener[ReqT] = {
    val started = System.nanoTime()
    next.startCall(new SimpleForwardingServerCall[ReqT, RespT](call) {
      override def close(status: Status, trailers: Metadata): Unit = {
        val protocol = "gRPC"
        val referred = NONE_INFO
        val bytesSent = NONE_INFO
        val statusCode = status.getCode.name()
        val ua = headers.get(GrpcUtil.USER_AGENT_KEY)
        val method = call.getMethodDescriptor.getType.name()
        val path = call.getMethodDescriptor.getFullMethodName
        val cost = (System.nanoTime() - started) / 1000000.0
        val now = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val requestId = Option(headers.get(MetadataKeys.REQUEST_ID_KEY)).getOrElse(NONE_INFO)
        val address = call.getAttributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR) match {
          case _: InProcessSocketAddress ⇒ "in-process-address"
          case addr: InetSocketAddress   ⇒ addr.getAddress.getHostAddress
        }
        val message = s"""$address $serviceName $requestId - [$now] "$method $path $protocol" $statusCode $bytesSent $cost "$referred" "$ua""""
        logger.info(message)
        super.close(status, trailers)
      }
    }, headers)
  }

}

object AccessLogInterceptor {

  def apply(): AccessLogInterceptor = new AccessLogInterceptor(None)

  def apply(service: String): AccessLogInterceptor = new AccessLogInterceptor(Option(service))

}
