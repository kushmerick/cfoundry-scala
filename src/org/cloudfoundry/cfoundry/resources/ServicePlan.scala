package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class ServicePlan(client: ClientContext) extends Resource(client) with ServicePlanJF {

  property("free", typ="bool")
  property("public", typ="bool", default = Some(true))

  hasA("service")
  hasMany("serviceInstance")

}
