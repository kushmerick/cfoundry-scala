package org.cloudfoundry.cfoundry.http

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.util._
import java.net._
import java.util.logging._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.entity._
import org.apache.http.entity.mime._
import org.apache.http.entity.mime.content._

class HttpCRUD(var _endpoint: String, val _logger: Logger = null) extends CRUD(_endpoint, _logger) {
  
  import CRUD._

  override def Crud(path: Path, headers: Option[Pairs], payload: Option[Chalice]): Response = {
    execute(classOf[HttpPost], path, headers, payload)
  }

  override def cRud(path: Path, headers: Option[Pairs]): Response = {
    execute(classOf[HttpGet], path, headers)
  }

  override def crUd(path: Path, headers: Option[Pairs], payload: Option[Chalice]): Response = {
    execute(classOf[HttpPut], path, headers, payload)
  }

  override def cruD(path: Path, headers: Option[Pairs]): Response = {
    execute(classOf[HttpDelete], path, headers)
  }

  ////////////////////

  private def execute[T <: HttpRequestBase](classs: Class[T], path: Path, headers: Option[Pairs]): Response = {
    execute(makeRequest(classs, path, headers))
  }
  
  private def execute[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, headers: Option[Pairs], payload: Option[Chalice]): Response = {
    execute(makeRequest(classs, path, headers, payload))
  }
    
  private val excerptLength = 4096

  protected def execute(request: HttpRequestBase): Response = try {
    trace(request)
    val response = new ExcerptableHttpResponse(httpClient.execute(request), excerptLength)
    trace(response)
    Response(response)
  } catch {
    case x: Exception => {
      request.abort
      throw new HTTPFailure(x)
    }      
  } finally {
    request.reset
  }

  private def makeRequest[T <: HttpRequestBase](classs: Class[T], path: Path, headers: Option[Pairs]): HttpRequestBase = {
    val request = classs.newInstance
    request.setURI(new URI(endpoint + makePath(path)))
    addHeaders(request, headers, Some(customHeaders))
    request
  }

  private def makeRequest[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, headers: Option[Pairs], payload: Option[Chalice]): HttpEntityEnclosingRequestBase = {
    val request = makeRequest(classs, path, headers).asInstanceOf[T]
    setPayload(request, payload)
    request
  }

  /////////////////////

  private def addHeaders(request: HttpRequest, headerSets: Option[Pairs]*): Unit = {
    val headers = Pairs.merge(
      headerSets.map(_ match {
        case Some(x) => x
        case None => Pairs()
      })
    )
    addHeaders(request, HttpCRUD.STANDARD_HEADERS ++ headers)
  }

  private def addHeaders(request: HttpRequest, headers: Pairs) = {
    for ((key, value) <- headers) {
      request.addHeader(key, value)
    }
  }

  private def setPayload(request: HttpEntityEnclosingRequest, payload: Option[Chalice]) = {
    if (payload.isDefined) {
      request.setEntity(makeEntity(payload.get))
    }
    request
  }
  
  private def makeEntity(payload: Chalice) = {
    if (payload.isSeq) {
      val e = new MultipartEntity 
      payload.seq.foreach(part => {
        e.addPart(part("name").string, contentBody(part(CT).string, part("body")))
      })
      e
    } else {
      new ByteArrayEntity(payload.blob)
    }
  }

  private def contentBody(contentType: String, body: Chalice) = {
    if (contentType == ctZIP) {
      new ByteArrayBody(body("bytes").blob, ctZIP, body("filename").string)
    } else if (contentType == ctJSON) {
      new StringBody(JSON.serialize(body("data")), ctJSON, UTF8)
    } else {
      throw new UnknownContentType(contentType)
    }
  }

  /////////////////////

  private def trace(request: HttpUriRequest): Unit = {
    var message = s"${request.getRequestLine}: headers=${headers(request)}"
    type E = HttpEntityEnclosingRequest
    if (request.isInstanceOf[E]) {
      val excerpt = Entity.excerpt(request.asInstanceOf[E], excerptLength)
      message += s"; payload=${excerpt}"
    }
    trace(s">> ${message}")
  }

  private def trace(response: ExcerptableHttpResponse): Unit = {
    val payload = if (response.getEntity != null) s", payload=${Entity.excerpt(response, excerptLength)}" else ""
    trace(s"<< code=${response.getStatusLine.getStatusCode}, headers=${headers(response)}${payload}")
  }

  private def trace(message: String): Unit = {
    if (logger != null) {
      logger.finer(s"${getClass.getName}: ${message}")
    }
  }

  private def headers(message: HttpMessage) = {
    message.getAllHeaders
      .map(header => s"${header.getName}=${header.getValue}")
      .toList
      .mkString("[", "; ", "]")
  }

  /////////////////////

  private val httpClient: HttpClient = new DefaultHttpClient

}

object HttpCRUD {

  def factory(endpoint: String, logger: Logger) = new HttpCRUD(endpoint, logger)

  private val STANDARD_HEADERS = Pairs(
    CT -> ctJSON,
    ACCEPT -> ctJSON)

}
