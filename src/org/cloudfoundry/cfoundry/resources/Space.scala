package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Space(client: ClientContext) extends Resource(client) with SpaceJF {

  hasA("organization")
  hasMany("serviceInstance")
  hasMany("apps")
  
  property("description", applicable = false)

}