package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class User(client: ClientContext) extends Resource(client) { // TODO with UserJF {

  property("admin", typ = "bool")
  hasMany("organization")
  hasMany("spaces")
  hasMany("apps")
  
  property("name", applicable = false)
  property("description", applicable = false)
  
}