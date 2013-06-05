package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class ServiceBinding(client: ClientContext) extends Resource(client) with ServiceBindingJF {

  hasA("app")
  hasA("serviceInstance")

}