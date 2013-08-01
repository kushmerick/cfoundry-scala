package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class SpacSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests {

  override val login = true

  "Space" should "be CRUDable" in { client =>
    testCRUD(client, "space", Map("organization" -> client.organizations(0)))
  }

  it should "be able to use a query to find itself" in { client =>
    testEnumerationId(client, "space", client.spaces(0))
  }
  
  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "space")
  }

}
