package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class ServiceInstance(client: ClientContext) extends Resource(client) {

  property("description", applicable = false)
  hasA("servicePlan")
  hasA("space")

}