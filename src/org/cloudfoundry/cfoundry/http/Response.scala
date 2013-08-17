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

  // just for spec; dual of 'unpack'
  def pack = {
    val packed = new HashMap[String, Any]
    if (hasCode) packed += CODE -> code.get
    if (hasPayload) {
      packed += (if (payload.isBlob) {
    	  BLOB -> B64.encodeAsString(payload.blob)
        } else {
          PAYLOAD -> payload
        }
      )
    }
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

  def apply(r: HttpResponse) = create(r)

  private def create(r: HttpResponse) = {
    val code = r.getStatusLine.getStatusCode
    val entity = r.getEntity
    val payload =
      if (entity != null) {
        try {
          val decode = getDecoder(entity.getContentType.getValue)
          Some(Chalice(decode(entity)))
        } catch {
          case x: Exception => throw new InvalidResponse(code, x)
        }
      } else {
        None
      }
    new Response(Some(code), payload)
  }

  // just for unit tests; dual of 'pack'
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
      } else if (packed.contains(BLOB)) {
        Some(Chalice(B64.decode(packed(BLOB).string)))
      } else {
        None
      }
    new Response(code, payload)
  }

  private val CODE = "code"
  private val PAYLOAD = "payload"
  private val BLOB = "blob"
    
  private def getDecoder(contentType: String) = {
    val sc = contentType.indexOf(';')
    var ct = if (sc > 0) contentType.substring(0, sc) else contentType
	DECODERS.get(ct) match {
	  case Some(decoder) => decoder
	  case None => blobDecoder _
    }
  }
    
  private val DECODERS = Map(
    "application/json" -> jsonDecoder _
  )
  
  // just some SWAGs....
  private val ENTITY_SIZE_GUESS = 2 << 20
  private val ENTITY_BUF_SIZE = 2 << 18
  
  private def asBytes(entity: HttpEntity) = {
	var N = entity.getContentLength.intValue // TODO: Huge payloads?!
	if (N < 0) N = ENTITY_SIZE_GUESS
    val blob = new ByteArrayOutputStream(N)
    val buf = Array.fill[Byte](Math.min(N,ENTITY_BUF_SIZE))(0)
    val istream = entity.getContent
    var n = 1
    while (n > 0) {
      n = istream.read(buf, 0, buf.size) 
      if (n > 0) blob.write(buf, 0, n)
    }
	blob.toByteArray
  }
  
  private def jsonDecoder(entity: HttpEntity) = try {
    val bytes = asBytes(entity)
    try {
      JSON.deserialize(new ByteArrayInputStream(bytes))
    } catch {
      // TODO: Hack/workaround for https://github.com/cloudfoundry/cloud_controller_ng/issues/79
      case x: Exception => bytes
    }
  }
  
  private def blobDecoder(entity: HttpEntity) = {
	asBytes(entity)
  } 

}