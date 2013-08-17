package org.cloudfoundry.cfoundry.http

import java.io._
import java.nio.charset._
import org.apache.http._
import org.apache.http.entity._
import org.cloudfoundry.cfoundry.util._

/* this functionality is only used for logging/debugging */

object Entity {

  def excerpt(request: HttpEntityEnclosingRequest, maxLength: Int): String = excerpt(request.getEntity, maxLength)

  def excerpt(response: HttpResponse, maxLength: Int): String = excerpt(response.getEntity, maxLength)

  private def excerpt(entity: HttpEntity, maxLength: Int): String = {
    try {
      // read directly from the entity's stream
      val buf = new Array[Byte](maxLength)
      val istream = entity.getContent
      istream.mark(buf.length)
      val nread = istream.read(buf)
      istream.reset
      if (nread < 1) {
        "<empty entity>"
      } else {
        for (i <- 0 until nread) if (buf(i) < ' ' || buf(i) > '~') buf(i) = '.' // ensure blobs are printable
        val ellipsis = if (nread == maxLength) "..." else ""
        new String(buf, 0, nread) + ellipsis
      }
    } catch {
      case x: UnsupportedOperationException =>
        // some entities (eg, multi-part) aren't don't provide access
        // to a content stream. TODO: Something better...
        s"<unreadable entity: ${x}>"
    }
  }
}