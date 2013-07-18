package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.config._
import java.util.logging._

class ServiceInstanceSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {
  
  override val login = true
  
  "ServiceInstance" should "should be CRUDable" in { _ =>
    // C
    val serviceInstance = client.serviceInstance.resource
    serviceInstance.space = client.spaces(0)
    serviceInstance.servicePlan = client.services(0).servicePlans(0)
    serviceInstance.name = "foobar"
    serviceInstance.save
    // R
    client.serviceInstances.contains(serviceInstance) should be(true)
    // U
    serviceInstance.name = "foobaz"
    serviceInstance.save
    serviceInstance.name.string should be("foobaz")
    // D
    serviceInstance.destroy
    client.serviceInstances.contains(serviceInstance) should be(false)
  }

}