package org.cloudfoundry.cfoundry.client.spec

import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.scalatest._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.config._
import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.scalatest.fixture._
import org.scalatest.matchers._
import java.util.logging._

class ClientSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {

  "Client" should "know its name" in { client =>
    client.name.string should equal(AbstractClient.name)
  }

  it should "know its description" in { client =>
    client.description.string should equal(AbstractClient.description)
  }
  
  it should "know its target" in { client =>
    client.target.string should equal(Config.cfTarget)
  }

  it should "know its version" in { client =>
    client.cfoundry_scala_version.string should equal(Version.version)
  }

  it should "know CF's version" in { client =>
    val CLOUDFOUNDRY_VERSION = 2 // umm, err, uhh, ...
    client.cloudfoundry_version.int should equal(CLOUDFOUNDRY_VERSION)
  }

  it should "not have a id" in { client =>
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "not have a url" in { client =>
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "support login" in { client =>
    client.login(Config.cfUsername, Config.cfPassword)
    client.authenticated should be(true)
  }

  it should "support logout" in { client =>
    client.logout
    client.authenticated should be(false)
  }
  
  it should "refresh the auth token" in { client =>
    client.login(Config.cfUsername, Config.cfPassword)
    val goodToken = client.getToken
    // simulate an expired token with a broken token
    val badToken = new Token(Chalice(goodToken.info.map + ("access_token" -> "foobar")))
    client.setToken(badToken)
    var refreshed = false
    client.setAuthenticator(() => {
      refreshed = true
      client.setToken(goodToken)
      true
    })
    client.organizations
    refreshed should be(true)
  }

}