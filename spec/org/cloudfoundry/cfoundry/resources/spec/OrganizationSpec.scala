package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class OrganizationSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with EnumerationTests {

  override val login = true

  it should "be able to use a query to find itself" in { client =>
    testEnumerationId(client, "organization", client.organizations(0))
  }

  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "organization")
  }

}
