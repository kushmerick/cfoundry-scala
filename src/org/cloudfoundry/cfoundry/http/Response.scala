package org.cloudfoundry.cfoundry.http

import java.io._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.util._

class Response(code: Option[Int] = None, _payload: Option[Payload] = None) {

  def payload = _payload match {
    case Some(x) => x
    case None => throw new NoPayload(this)
  }

  def ok: Boolean = code match {
    case None => false
    case Some(c) => c >= 200 && c < 300
  }

  override def toString = {
    val c = code match { case Some(c) => c; case None => "-" }
    val s = if (ok) "ok" else "error"
    s"<Response $c ($s): ${payload.toString}>"
  }

}

object Response {

  def apply(r: ExcerptableHttpResponse) = create(r)

  private def create(r: ExcerptableHttpResponse) = {
    val code = r.getStatusLine.getStatusCode
    var payload = Payload(null)
    if (r.hasEntity) {
      var istream: InputStream = null
      try {
        istream = r.getEntity.getContent
        payload = new Payload(JSON.deserialize(istream))
      } catch {
        case x: Exception => throw new InvalidResponse(code, x)
      } finally {
        if (istream != null) istream.close
      }
    }
    new Response(Some(code), Some(payload))
  }

}