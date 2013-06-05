package org.cloudfoundry.cfoundry.http.util

import org.apache.http._

class ExcerptableHttpResponse(response: HttpResponse, maxExcerpt: Int) extends HttpResponse {

  def getAllHeaders = response.getAllHeaders
  def getStatusLine = response.getStatusLine

  private val rawEntity = response.getEntity
  private val entity = if (rawEntity == null) rawEntity else new RepeatableHttpEntity(rawEntity, maxExcerpt)
  def hasEntity = entity != null
  override def getEntity = entity

  // TODO: Yuck -- How can we implement automatic delegation?!
  def addHeader(x$1: String, x$2: String): Unit = ???
  def addHeader(x$1: Header): Unit = ???
  def containsHeader(x$1: String): Boolean = ???
  def getFirstHeader(x$1: String): Header = ???
  def getHeaders(x$1: String): Array[Header] = ???
  def getLastHeader(x$1: String): Header = ???
  def getParams(): params.HttpParams = ???
  def getProtocolVersion(): ProtocolVersion = ???
  def headerIterator(x$1: String): HeaderIterator = ???
  def headerIterator(): HeaderIterator = ???
  def removeHeader(x$1: Header): Unit = ???
  def removeHeaders(x$1: String): Unit = ???
  def setHeader(x$1: String, x$2: String): Unit = ???
  def setHeader(x$1: Header): Unit = ???
  def setHeaders(x$1: Array[Header]): Unit = ???
  def setParams(x$1: params.HttpParams): Unit = ???
  def getLocale(): java.util.Locale = ???
  def setEntity(x$1: HttpEntity): Unit = ???
  def setLocale(x$1: java.util.Locale): Unit = ???
  def setReasonPhrase(x$1: String): Unit = ???
  def setStatusCode(x$1: Int): Unit = ???
  def setStatusLine(x$1: ProtocolVersion, x$2: Int, x$3: String): Unit = ???
  def setStatusLine(x$1: ProtocolVersion, x$2: Int): Unit = ???
  def setStatusLine(x$1: StatusLine): Unit = ???

}