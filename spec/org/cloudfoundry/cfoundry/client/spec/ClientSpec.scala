package org.cloudfoundry.cfoundry.client.spec

import org.cloudfoundry.cfoundry._
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
import org.cloudfoundry.cfoundry.client.mock.MockedClientFixture

class ClientSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {

  "Client" should "know its name" in { client =>
    client.name.string should equal(AbstractClient.name)
  }

  it should "know its description" in { client =>
    client.description.string should equal(AbstractClient.description)
  }

  it should "know its url" in { client =>
    client.url.string should equal(AbstractClient.repo)
  }

  it should "know its target" in { client =>
    client.target.string should equal(Config.cfTarget)
  }

  it should "know its version" in { client =>
    client.version.string should equal(Version.version)
  }

  it should "know CF's version" in { client =>
    val CLOUDFOUNDRY_VERSION = 2 // umm, err, uhh, ...
    client.cloudfoundryVersion.int should equal(CLOUDFOUNDRY_VERSION)
  }

  it should "not have a id" in { client =>
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
  
  it should "support custom headers" in { client =>
    val crud = new MockHttpCRUD
    client.setCrud(crud)
    client.customHeaders = Pairs("X-Foo" -> "bar")
    val headers = client.customHeaders.pairs
    headers.size should equal(1)
    headers("X-Foo") should equal("bar")
    crud.cRud(Left("/foo"))(None)
    var headers2 = crud.lastRequest.getHeaders("X-Foo")
    headers2.length should equal(1)
    headers2(0).getValue should equal("bar")
    client.clearCustomHeaders
    crud.cRud(Left("/foo"))(None)
    headers2 = crud.lastRequest.getHeaders("X-Foo")
    headers2.length should equal(0)
  }

}