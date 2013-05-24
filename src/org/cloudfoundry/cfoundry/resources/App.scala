package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class App(client: ClientContext) extends Resource(client) {

  hasA("space")

}