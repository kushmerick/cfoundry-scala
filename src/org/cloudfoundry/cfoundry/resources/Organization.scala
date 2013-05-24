package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class Organization(client: ClientContext) extends Resource(client) {

  hasMany("space")

}
