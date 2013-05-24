package org.cloudfoundry.cfoundry.v2.client.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.v2.client._
import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.client._
import java.util.logging._

class ClientSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  val logger = Logger.getGlobal
  logger.addHandler(new ConsoleHandler)
  logger.setLevel(Level.FINEST)

  class MockedClient(target: String) extends AbstractClient[MockCRUD](MockCRUD.factory, target, logger)

  var client: MockedClient = null

  before {
    client = new MockedClient("http://api.cloudfoundry.example.com")
  }

  after {
  }

  "Client" should "support login" in {
    client.login("a", "b") should equal(null)
  }

  it should "support logout" in {
    client.logout should equal()
  }

}