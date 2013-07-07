package org.cloudfoundry.cfoundry.client.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.config._
import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

class ClientSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  val logger = Logger.getGlobal
  logger.setLevel(Level.FINEST)

  var client: MockedClient = null

  before {
    client = new MockedClient(logger)
    client.startup
  }

  after {
    client.shutdown
  }

  "Client" should "know its name" in {
    client.name.string should equal(AbstractClient.name)
  }

  it should "know its description" in {
    client.description.string should equal(AbstractClient.description)
  }

  it should "know its version" in {
    client.cfoundry_scala_version.string should equal(Version.version)
  }

  it should "know CF's version" in {
    val CLOUDFOUNDRY_VERSION = 2 // umm, err, uhh, ...
    client.cloudfoundry_version.int should equal(CLOUDFOUNDRY_VERSION)
  }

  it should "not have a id" in {
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "not have a url" in {
    intercept[InvalidProperty] {
      client.id
    }
  }

  it should "support login" in {
    client.login(Config.cfUsername, Config.cfPassword)
    client.authenticated should be(true)
  }

  it should "support logout" in {
    client.logout
    client.authenticated should be(false)
  }

}