package org.cloudfoundry.cfoundry.http.mock

import org.apache.http.message._
import org.apache.http.entity._
import org.apache.http._

class MockHttpResponse(code: Int = 200, bytes: Array[Byte] = Array[Byte](), contentType: String = "application/json")
  extends BasicHttpResponse(MockHttpResponse.statusLine(code)) {
  
  override def getEntity = new ByteArrayEntity(bytes, ContentType.create(contentType))

}

object MockHttpResponse {
  
  def statusLine(code: Int) = new BasicStatusLine(new ProtocolVersion("1", 0, 0), code, null)
  
}
