package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class App(client: ClientContext) extends Resource(client) with AppJF {

  hasA("space")

}