package org.cloudfoundry.cfoundry.http

import java.io._
import java.nio.charset._
import org.apache.http._
import org.apache.http.entity._

object Entity {
  
  def excerpt(request: HttpEntityEnclosingRequest, maxLength: Int): String = excerpt(request.getEntity, maxLength)
  
  def excerpt(response: HttpResponse, maxLength: Int): String = excerpt(response.getEntity, maxLength)
  
  private def excerpt(entity: HttpEntity, maxLength: Int): String = {
    val istream = entity.getContent

    istream.mark(maxLength)
    val buf = new Array[Byte](maxLength)
    val nread = istream.read(buf)
    istream.reset
    if (nread < 1) {
      ""
    } else {
      var charset = ContentType.getOrDefault(entity).getCharset
      if (charset == null) charset = UTF8 // sometimes "getOrDefault" doesn't
      new String(buf, 0, nread, charset)
    }
  }
  
  lazy private val UTF8 = Charset.forName("UTF-8")

}