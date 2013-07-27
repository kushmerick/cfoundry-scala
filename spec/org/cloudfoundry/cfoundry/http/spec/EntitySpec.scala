package org.cloudfoundry.cfoundry.http.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.http.mock._
import org.apache.http.message._
import org.apache.http.entity._

class EntitySpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  "Entity" should "excerpt short requests" in {
    testRequest(10, "foo1234", "foo1234")
  }

  it should "excerpt long requests" in {
    testRequest(3, "foo1234", "foo...")
  }
  
  def testRequest(maxLength: Int, payload: String, excerpt: String) = {
    val request = new BasicHttpEntityEnclosingRequest("GET", "http://foobar")
    val entity = new StringEntity(payload)
    request.setEntity(entity)
    Entity.excerpt(request, maxLength) should equal(excerpt)
  }
  
   it should "excerpt short responses" in {
    testResponse(10, "foo1234", "foo1234")
  }

  it should "excerpt long responses" in {
    testResponse(3, "foo1234", "foo...")
  }
  
  def testResponse(maxLength: Int, payload: String, excerpt: String) = {
    val response = new MockHttpResponse(bytes = payload.getBytes)
    Entity.excerpt(response, maxLength) should equal(excerpt)
  }
  
}