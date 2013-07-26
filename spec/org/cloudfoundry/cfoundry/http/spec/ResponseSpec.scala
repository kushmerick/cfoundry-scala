package org.cloudfoundry.cfoundry.http.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.http.mock._

class ResponseSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  "Response" should "support pack/unpack" in {
    val payload = Chalice(Map("foo" -> "bar"))
    val response1 = new Response(Some(1234), Some(payload))
    val response2 = Response.unpack(response1.pack)
    response1 should equal(response2)
  }
  
  it should "support JSON payloads" in {
    val obj = Seq(1,2,3,4)
	val response = Response(new MockHttpResponse(bytes = JSON.serialize(Chalice(obj)).getBytes))
    response.payload.raw should equal(obj)
  }

  it should "support blob payloads" in {
    val obj = Array[Byte](1,2,3,4)
    val response = Response(new MockHttpResponse(bytes = obj, contentType = null))
    response.payload.blob should equal(obj)
  }

}