package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._
import org.scalatest.matchers._
import org.scalatest.fixture._

class SpaceSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true

  "Space" should "be CRUDable" in { client =>
    give an "organization" from client to { org =>
      testCRUD(client, "space", Map("organization" -> org))
    }
  }

  it should "be able to use a query to find itself" in { client =>
    give a "space" from client to { space =>
      testEnumerationId(client, "space", space)
    }
  }
  
  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "space")
  }

}
