package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Organization(client: ClientContext) extends Resource(client) with HasAppendages with OrganizationJF {

  property("description", applicable = false)

  hasMany("space")

  class Members extends Roles(this)
  val members = new Members

  class Managers extends Roles(this)
  val managers = new Managers

  class BillingManagers extends Roles(this)
  val billingManagers = new BillingManagers

  class Auditors extends Roles(this)
  val auditors = new Auditors

  // for "org.managers = foobar" etc
  def members_=(_members: Roles.GUIDs) = members.set(_members)
  def managers_=(_managers: Roles.GUIDs) = managers.set(_managers)
  def billingManagers_=(_billingManagers: Roles.GUIDs) = billingManagers.set(_billingManagers)
  def auditors_=(_auditors: Roles.GUIDs) = auditors.set(_auditors)

}
