package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class Service(client: ClientContext) extends Resource(client) {

  property("name", source = "label")
  property("provider")
  property("version")
  property("active", typ = "bool")
  hasMany("servicePlan")

}