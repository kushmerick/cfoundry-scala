package org.cloudfoundry.cfoundry.http

import java.io._
import java.net._
import java.util.logging._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.util._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.entity._

class HttpCRUD(var _endpoint: String, val _logger: Logger = null) extends CRUD(_endpoint, _logger) {
  
  import CRUD._

  override def Crud(path: Path, headers: Option[Pairs], payload: Option[String]): Response = {
    execute(classOf[HttpPost], path, headers, payload)
  }

  override def cRud(path: Path, headers: Option[Pairs]): Response = {
    execute(classOf[HttpGet], path, headers)
  }

  override def crUd(path: Path, headers: Option[Pairs], payload: Option[String]): Response = {
    execute(classOf[HttpPut], path, headers, payload)
  }

  override def cruD(path: Path, headers: Option[Pairs]): Response = {
    execute(classOf[HttpDelete], path, headers)
  }

  ////////////////////

  val excerptLength = 4096

  private def execute[T <: HttpRequestBase](classs: Class[T], path: Path, headers: Option[Pairs]) = {
    val request = makeRequest(classs, path, headers)
    trace(request)
    var rawResponse: HttpResponse = null
    try {
      rawResponse = httpClient.execute(request)
    } catch {
      case x: Exception => throw new HTTPFailure(x)
    }
    val response = new ExcerptableHttpResponse(rawResponse, excerptLength)
    trace(response)
    Response(response)
  }

  private def execute[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, headers: Option[Pairs], payload: Option[String]) = {
    val request = makeRequest(classs, path, headers, payload)
    trace(request)
    val response = new ExcerptableHttpResponse(httpClient.execute(request), excerptLength)
    trace(response)
    Response(response)
  }

  private def makeRequest[T <: HttpRequestBase](classs: Class[T], path: Path, headers: Option[Pairs]): HttpUriRequest = {
    val request = classs.newInstance
    request.setURI(new URI(endpoint + makePath(path)))
    addHeaders(request, headers)
    request
  }

  private def makeRequest[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, headers: Option[Pairs], payload: Option[String]): HttpUriRequest = {
    val request = makeRequest(classs, path, headers).asInstanceOf[T]
    setPayload(request, payload)
    request
  }

  /////////////////////

  private def addHeaders(request: HttpRequest, headers: Option[Pairs]): Unit = {
    var opts =
      HttpCRUD.STANDARD_HEADERS ++
        (headers match {
          case Some(x) => x
          case None => Pairs()
        })
    addHeaders(request, opts)
  }

  private def addHeaders(request: HttpRequest, headers: Pairs) = {
    for ((key, value) <- headers) {
      request.addHeader(key, value)
    }
  }

  private def setPayload(request: HttpEntityEnclosingRequest, payload: Option[String]) = {
    payload match {
      case Some(payload) => request.setEntity(new StringEntity(payload))
      case None =>
    }
    request
  }

  /////////////////////

  private def trace(request: HttpUriRequest): Unit = {
    var message = s"${request.getRequestLine}: headers=${headers(request)}"
    type E = HttpEntityEnclosingRequest
    if (request.isInstanceOf[E]) {
      val excerpt = Entity.excerpt(request.asInstanceOf[E], maxExcerpt)
      message += s"; payload=${excerpt}"
    }
    trace(s">> ${message}")
  }

  private def trace(response: ExcerptableHttpResponse): Unit = {
    val payload = if (response.hasEntity) s", payload=${Entity.excerpt(response, maxExcerpt)}" else ""
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

  val maxExcerpt = 4096

  /////////////////////

  private val httpClient: HttpClient = new DefaultHttpClient

}

object HttpCRUD {

  def factory(endpoint: String, logger: Logger) = new HttpCRUD(endpoint, logger)

  val FORM_ENCODED = "application/x-www-form-urlencoded"

  private val JSON = "application/json"

  private val STANDARD_HEADERS = Pairs(
    "Content-Type" -> JSON,
    "Accept" -> JSON)

}
