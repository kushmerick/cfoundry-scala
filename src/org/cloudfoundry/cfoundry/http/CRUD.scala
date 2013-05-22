package org.cloudfoundry.cfoundry.http

import java.io._
import java.net._
import java.util.logging._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http.resettable._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.entity._

class CRUD(var endpoint: String, val logger: Logger = null) {

  def create(path: PathComponent*)(options: Option[Pairs] = None)(payload: Option[Payload] = None): Response = {
    create(path, options, payload)
  }
  def create(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    execute(classOf[HttpPost], path, options, payload)
  }

  def read(path: PathComponent*)(options: Option[Pairs] = None): Response = {
    read(path, options)
  }
  def read(path: Path, options: Option[Pairs]): Response = {
    execute(classOf[HttpGet], path, options)
  }

  def update(path: PathComponent*)(options: Option[Pairs] = None)(payload: Option[Payload] = None): Response = {
    update(path, options, payload)
  }
  def update(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    execute(classOf[HttpPut], path, options, payload)
  }

  def delete(path: PathComponent*)(options: Option[Pairs] = None): Response = {
    delete(path, options)
  }
  def delete(path: Path, options: Option[Pairs]): Response = {
    execute(classOf[HttpDelete], path, options)
  }

  ////////////////////

  type PathComponent = Either[String, Iterable[String]]
  type Path = Iterable[PathComponent]

  def makePath(path: Path) = {
    path
    .map(component => component match { case Left(s) => s; case Right(sseq) => sseq.mkString("/") })
    .mkString("/")
  }

  ////////////////////

  val excerptLength = 4096

  private def execute[T <: HttpRequestBase](classs: Class[T], path: Path, options: Option[Pairs]) = {
    val request = makeRequest(classs, path, options)
    trace(request)
    val response = new ResettableHttpResponse(httpClient.execute(request), excerptLength)
    trace(response)
    Response(response)
  }

  private def execute[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, options: Option[Pairs], payload: Option[Payload]) = {
    val request = makeRequest(classs, path, options, payload)
    trace(request)
    val response = new ResettableHttpResponse(httpClient.execute(request), excerptLength)
    trace(response)
    Response(response)
  }

  private def makeRequest[T <: HttpRequestBase](classs: Class[T], path: Path, options: Option[Pairs]): HttpUriRequest = {
    val request = classs.newInstance
    request.setURI(new URI(endpoint + makePath(path)))
    addOptions(request, options)
    request
  }

  private def makeRequest[T <: HttpEntityEnclosingRequestBase](classs: Class[T], path: Path, options: Option[Pairs], payload: Option[Payload]): HttpUriRequest = {
    val request = makeRequest(classs, path, options).asInstanceOf[T]
    setPayload(request, payload)
    request
  }

  /////////////////////

  private def addOptions(request: HttpRequest, options: Option[Pairs]): Unit = {
    var opts =
      CRUD.STANDARD_HEADERS ++
        (options match {
          case Some(x) => x
          case None => Pairs()
        })
    addOptions(request, opts)
  }

  private def addOptions(request: HttpRequest, options: Pairs) = {
    for ((key, value) <- options) {
      request.addHeader(key, value)
    }
  }

  private def setPayload(request: HttpEntityEnclosingRequest, payload: Option[Payload]) = {
    payload match {
      case Some(payload) => request.setEntity(new StringEntity(payload.string))
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
    trace(s">>>> ${message}")
  }

  private def trace(response: HttpResponse): Unit = {
    trace(s"<<<< code=${response.getStatusLine.getStatusCode}, headers=${headers(response)}, payload=${Entity.excerpt(response, maxExcerpt)}")
  }

  private def trace(message: String): Unit = {
    if (logger != null) {
      logger.fine(s"${getClass.getName}: ${message}")
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

  if (endpoint == null) throw new NoEndpoint
  if (endpoint.last == '/') endpoint = endpoint.substring(0, endpoint.length)

  /////////////////////

  private val httpClient: HttpClient = new DefaultHttpClient

}

object CRUD {

  def factory(base: String, logger: Logger = null) = new CRUD(base, logger)

  val FORM_ENCODED = "application/x-www-form-urlencoded"

  private val JSON = "application/json"

  private val STANDARD_HEADERS = Pairs(
    "Content-Type" -> JSON,
    "Accept" -> JSON)

}
