package org.jmotor.grpc

import io.grpc.Metadata

/**
 *
 * @author AI
 *         2019-06-28
 */
object MetadataKeys {

  lazy final val REQUEST_ID_KEY = Metadata.Key.of[String]("x-request-id", Metadata.ASCII_STRING_MARSHALLER)

}
