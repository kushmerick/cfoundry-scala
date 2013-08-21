package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._

class OrganizationSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true
  
  "Organization" should "be CRUDable" in { client =>
    testCRUD(client, "organization")
  }

  it should "be able to use a query to find itself" in { client =>
    give an "organization" from client to { org =>
      testEnumerationId(client, "organization", org)
    }
  }

  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "organization")
  }

}
