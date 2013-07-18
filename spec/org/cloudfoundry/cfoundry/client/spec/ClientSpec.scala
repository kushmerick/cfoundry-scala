package org.cloudfoundry.cfoundry.client.spec

import org.scalatest.fixture._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.scalatest._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.config._
import org.cloudfoundry.cfoundry.http.mock._
import java.util.logging._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.exceptions._

class ClientSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {

  "Client" should "know its name" in { _ =>
    client.name.string should equal(AbstractClient.name)
  }

  it should "know its description" in { _ =>
    client.description.string should equal(AbstractClient.description)
  }
  
  it should "know its target" in { _ =>
    client.target.string should equal(Config.cfTarget)
  }

  it should "know its version" in { _ =>
    client.cfoundry_scala_version.string should equal(Version.version)
  }

  it should "know CF's version" in { _ =>
    val CLOUDFOUNDRY_VERSION = 2 // umm, err, uhh, ...
    client.cloudfoundry_version.int should equal(CLOUDFOUNDRY_VERSION)
  }

  it should "not have a id" in { _ =>
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "not have a url" in { _ =>
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "support login" in { _ =>
    client.login(Config.cfUsername, Config.cfPassword)
    client.authenticated should be(true)
  }

  it should "support logout" in { _ =>
    client.logout
    client.authenticated should be(false)
  }

}