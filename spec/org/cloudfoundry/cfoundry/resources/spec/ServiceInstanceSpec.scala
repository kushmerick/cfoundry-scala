package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class ServiceInstanceSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests {

  override val login = true

  "ServiceInstance" should "be CRUDable" in { client =>
    testCRUD(client, "serviceInstance", Map("space" -> client.spaces(0), "servicePlan" -> client.services(0).servicePlans(0)))
  }

  it should "be able to use a query to find itself" in { client =>
    val xxx = client.serviceInstances.resources
    val serviceInstance = client.serviceInstance.resource
    serviceInstance.name = "blah"
    serviceInstance.space = client.spaces(0)
    serviceInstance.servicePlan = client.services(0).servicePlans(0)
    serviceInstance.save
    testEnumeration(client, "serviceInstance", serviceInstance)
    serviceInstance.destroy
  }

}