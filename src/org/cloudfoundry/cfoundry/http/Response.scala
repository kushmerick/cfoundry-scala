package org.cloudfoundry.cfoundry.http

import java.io._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.util._
import scala.collection.mutable._

class Response(code: Option[Int] = None, _payload: Option[Chalice] = None) {

  import Response._

  def hasPayload = _payload.isDefined
  def hasCode = code.isDefined

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

  // dual of 'unpack'
  def pack = {
    val packed = new HashMap[String, Any]
    if (hasCode) packed += CODE -> code.get
    if (hasPayload) packed += PAYLOAD -> payload
    Chalice(packed)
  }

}

object Response {

  def apply(r: ExcerptableHttpResponse) = create(r)

  private def create(r: ExcerptableHttpResponse) = {
    val code = r.getStatusLine.getStatusCode
    val payload =
      if (r.hasEntity) {
        var istream: InputStream = null
        try {
          istream = r.getEntity.getContent
          Some(Chalice(JSON.deserialize(istream)))
        } catch {
          case x: Exception => throw new InvalidResponse(code, x)
        } finally {
          if (istream != null) istream.close
        }
      } else {
        None
      }
    new Response(Some(code), payload)
  }

  // dual of 'pack'
  def unpack(encoded: Chalice) = {
    val packed = encoded.map
    val code =
      if (packed.contains(CODE)) {
        Some(packed(CODE).int)
      } else {
        None
      }
    val payload =
      if (packed.contains(PAYLOAD)) {
        Some(packed(PAYLOAD))
      } else {
        None
      }
    new Response(code, payload)
  }

  val CODE = "code"
  val PAYLOAD = "payload"

}