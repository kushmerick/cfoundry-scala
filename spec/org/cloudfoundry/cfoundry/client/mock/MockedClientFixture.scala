package org.cloudfoundry.cfoundry.client.mock

import org.scalatest.fixture.Suite
import org.scalatest.BeforeAndAfter
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.config._
import java.util.logging._

trait MockedClientFixture extends Suite with BeforeAndAfter {

  private var client: MockedClient = null

  protected val login = false

  protected def customBefore = {}
  protected def customAfter = {}
  
  before {
    client = new MockedClient(Logger.getGlobal())
    client.startup
    client.beginTest(s"before-${suiteName}")
    if (login) client.login(Config.cfUsername, Config.cfPassword)
    customBefore
  }

  after {
    client.beginTest(s"after-${suiteName}")
    customAfter
    if (login) client.logout
    client.shutdown
    client = null
  }

  type FixtureParam = MockedClient

  override def withFixture(test: OneArgTest) = {
    client.beginTest(test.name)
    withFixture(test.toNoArgTest(client))
  }
  
}
