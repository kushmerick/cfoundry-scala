package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._
import org.scalatest.matchers._
import org.scalatest.fixture._

class ServiceInstanceSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true
  
  private def pickPlan(client: FixtureParam) = try {
    // we can't use ResourceFixture to create servicePlan, because the
    // fake service gateway URL does not exist.  so this test selects
    // some genuine service plan from the server, and it marks itself
    // pending if it can't find one.
    client.services(first = true).resource.servicePlans(first = true).resource
  } catch {
    case x: Exception => null
  }
    
  "ServiceInstance" should "be CRUDable" in { client =>
    val servicePlan = pickPlan(client)
    if (servicePlan == null) {
      pending
    } else {
      give a "space" from client to { space =>
        testCRUD(client, "serviceInstance", Map("space" -> space, "servicePlan" -> servicePlan))
      }
    }
  }

  it should "be able to use a query to find itself" in { client =>
    val servicePlan = pickPlan(client)
    if (servicePlan == null) {
      pending
    } else {
      give a "space" from client to { space =>
        val serviceInstance = client.serviceInstance.resource
        serviceInstance.name = "testname"
        serviceInstance.servicePlan = servicePlan
        serviceInstance.space = space
        try {
          serviceInstance.save
        } catch {
          case x: Exception => // TODO: Do something with this exception?
        }
        if (serviceInstance.isLocalOnly) {
          // 'save' above raised an exception
          pending
        } else {
          var exception: Exception = null
          try {
            testEnumerationId(client, "serviceInstance", serviceInstance)
          } catch {
            case x: Exception => exception = x
          } finally {
            try {
              serviceInstance.destroy
            } catch {
              case x: Exception => // TODO: Do something with this exception?
            }
          }
          if (exception != null) throw exception
        }
      }
    }
  }
  
  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "serviceInstance")
  }

}