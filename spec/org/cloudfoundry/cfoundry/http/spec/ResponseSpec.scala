package org.cloudfoundry.cfoundry.http.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.http._

class ResponseSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  "Response" should "support pack/unpack" in {
    val response1 = new Response(Some(1234), Some(Chalice(Map("foo" -> "bar"))))
    val response2 = Response.unpack(response1.pack)
    response1 should equal(response2)
  }

}