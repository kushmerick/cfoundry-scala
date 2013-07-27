package org.cloudfoundry.cfoundry.auth.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.exceptions._

class TokenSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

 "Token" should "generate auth_header" in {
    val token = new Token(new Chalice(Map("token_type" -> "foo", "access_token" -> "bar")))
    token.authHeader should equal("foo bar")
  }
 
 it should "not generate auth_header if not authenticated" in {
   val token = ClientContext.UNAUTHENTICATED
   intercept[NotAuthenticated] {
     token.authHeader
   }
 }
 
 it should "decode the access token" in {
   val payload = Map("bar" -> "baz")
   val encodedPayload = Token.b64.encodeAsString(JSON.serialize(Chalice(payload)).getBytes)
   val accessToken = s"header.${encodedPayload}.crypto"
   val token = new Token(new Chalice(Map("token_type" -> "foo", "access_token" -> accessToken)))
   token.decodedPayload.raw should equal(payload)
 }

}