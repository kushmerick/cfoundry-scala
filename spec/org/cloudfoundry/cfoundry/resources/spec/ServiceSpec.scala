package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.client.mock.MockedClientFixture

class ServiceSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true

  "Service" should "be CRUDable" in { client =>
    testCRUD(
      client,
      "service",
      Map(
        "description" -> "testcrud_description",
        "provider" -> "testcrud_provider",
        "version" -> "testcrud_version",
        "active" -> true,
        "gatewayUrl" -> "http://testcrud_gatewayUrl"
      )
    )
  }

  it should "be able to use a query to find itself" in { client =>
    give a "service" from client to { service =>
      testEnumerationId(client, "service", service)
    }
  }
  
  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "service")
  }
  
}
