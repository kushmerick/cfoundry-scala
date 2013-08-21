package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._
import org.scalatest.matchers._
import org.scalatest.fixture._

class ServicePlanSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true

  "ServicePlan" should "be CRUDable" in { client =>
    give a "service" from client to { service =>
      testCRUD(client, "servicePlan", Map("service" -> service, "description" -> "testcrud_servicePlan_description", "free" -> true))
    }
  }

  it should "be able to use a query to find itself" in { client =>
    give a "servicePlan" from client to { servicePlan =>
      testEnumerationId(client, "servicePlan", servicePlan)
    }
  }

  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "servicePlan")
  }
  
}