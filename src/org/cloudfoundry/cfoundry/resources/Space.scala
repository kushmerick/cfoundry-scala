package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class Space(client: ClientContext) extends Resource(client) {

  hasA("organization")
  hasMany("serviceInstance")

}