package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class ServiceAuthToken(client: ClientContext) extends Resource(client) {

  property("label")
  property("provider")
  property("token")

  // TODO: Add additional hierarchy under resource to
  // avoid needing to remove these properties
  property("id", applicable = false)
  property("name", applicable = false)
  property("description", applicable = false)
  property("url", applicable = false)

}