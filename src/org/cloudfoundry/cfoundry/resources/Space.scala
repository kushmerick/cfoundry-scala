package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Space(client: ClientContext) extends Resource(client) with HasAppendages with SpaceJF {

  hasA("organization")
  hasMany("serviceInstance")
  hasMany("apps")
  
  property("description", applicable = false)
  
  class Managers extends Roles(this)
  val managers = new Managers
  
  class Developers extends Roles(this)
  val developers = new Developers
  
  class Auditors extends Roles(this)
  val auditors = new Auditors

  // for "space.managers = foobar" etc
  def managers_=(_managers: Roles.GUIDs) = managers.set(_managers)
  def developers_=(_developers: Roles.GUIDs) = developers.set(_developers)
  def auditors_=(_auditors: Roles.GUIDs) = auditors.set(_auditors)

}