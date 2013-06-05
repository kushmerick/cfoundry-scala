package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class ServiceInstance(client: ClientContext) extends Resource(client) with ServiceInstanceJF {

  property("description", applicable = false)
  hasA("servicePlan")
  hasA("space")

}