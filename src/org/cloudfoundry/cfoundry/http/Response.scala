package org.cloudfoundry.cfoundry.http

import java.io._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.util._
import scala.collection.mutable._
import org.apache.http._

class Response(val code: Option[Int] = None, _payload: Option[Chalice] = None) {

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
    val s = if (ok) "ok" else "error"
    val c = code match { case Some(c) => c.toString; case None => "no code" }
    val p = if (hasPayload) payload.toString else "no payload"
    s"<Response $c ($s): ${p}>"
  }

  // dual of 'unpack'
  def pack = {
    val packed = new HashMap[String, Any]
    if (hasCode) packed += CODE -> code.get
    if (hasPayload) packed += PAYLOAD -> payload
    Chalice(packed)
  }
  
  override def equals(x: Any) = try {
    val r = x.asInstanceOf[Response]
    code == r.code && payload==r.payload
  } catch {
    case x: Exception => false
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
          val entity = r.getEntity
          val decoder = getDecoder(entity.getContentType.getValue)
          Some(Chalice(decoder(entity)))
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

  private val CODE = "code"
  private val PAYLOAD = "payload"
    
  private def getDecoder(contentType: String) = {
    val sc = contentType.indexOf(';')
    var ct = if (sc > 0) contentType.substring(0, sc) else contentType
	DECODERS.get(ct) match {
	  case Some(decoder) => decoder
	  case None => blobDecoder _
    }
  }
    
  private val DECODERS = Map(
    "application/json" -> ((entity: HttpEntity) => JSON.deserialize(entity.getContent))
  )
  
  private def blobDecoder(entity: HttpEntity) = {
	val N = 4096
    val blob = new ByteArrayOutputStream(N<<2)
    val buf = Array.fill[Byte](N)(0)
    val istream = entity.getContent
    var n = 1
    while (n > 0) {
      n = istream.read(buf, 0, buf.size) 
      if (n > 0) blob.write(buf, 0, n)
    }
    blob.toByteArray
  } 

}