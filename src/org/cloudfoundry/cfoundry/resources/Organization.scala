package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Organization(client: ClientContext) extends Resource(client) with OrganizationJF {

  hasMany("space")

}
