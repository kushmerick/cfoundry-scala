package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class ServicePlan(client: ClientContext) extends Resource(client) {

  hasA("service")
  hasMany("serviceInstance")

}
