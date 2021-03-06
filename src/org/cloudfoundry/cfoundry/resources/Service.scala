package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Service(client: ClientContext) extends Resource(client) with ServiceJF {

  property("name", source = "label")
  property("provider")
  property("version")
  property("active", typ = "bool")
  property("gatewayUrl", typ = "url", source = "url") // ugghh: a service has two fields called "url"
  hasMany("servicePlan")

}
