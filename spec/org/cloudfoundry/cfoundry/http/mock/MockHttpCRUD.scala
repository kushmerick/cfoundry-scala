package org.cloudfoundry.cfoundry.http.mock

import org.cloudfoundry.cfoundry.http._
import java.util.logging._
import org.apache.http._
import org.apache.http.client.methods._

/*
 * MockHTTPCRUD doesn't actually fetch anything like MockCRUD.  All operations just
 * save the last request for a test to inspect.
*/

class MockHttpCRUD extends HttpCRUD("http://nowhere.com", Logger.getGlobal) {

  var lastRequest: HttpRequest = null
  
  override def execute(request: HttpRequestBase): Response = {
    lastRequest = request
    null
  }

}
