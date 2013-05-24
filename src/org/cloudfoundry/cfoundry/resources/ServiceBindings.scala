package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class ServiceBinding(client: ClientContext) extends Resource(client) {

  hasA("app")
  hasA("serviceInstance")

}