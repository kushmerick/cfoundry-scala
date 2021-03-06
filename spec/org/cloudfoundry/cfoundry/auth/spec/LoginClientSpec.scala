package org.cloudfoundry.cfoundry.client.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.config._
import org.cloudfoundry.cfoundry.exceptions._
import org.scalatest.fixture._
import org.scalatest.matchers._

class LoginClientSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {

  "LoginClient" should "support refresh" in { client =>
    val username = Config.cfUsername
    val token = client.loginClient.login(username, Config.cfPassword)
    client.loginClient.refresh(token) match {
      case Some(tok) => tok.decodedPayload("user_name").string should equal(username)
      case None => throw new CFoundryException("refresh failed")
    }
  }

}