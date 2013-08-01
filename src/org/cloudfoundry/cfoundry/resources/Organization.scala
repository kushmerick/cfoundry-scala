package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._

class Organization(client: ClientContext) extends Resource(client) with HasAppendages with OrganizationJF {

  hasMany("space")
  
  class Members extends Roles(this)
  val members = new Members
  def members_=(_members: Roles.GUIDs) = members() = _members

  class Managers extends Roles(this)
  val managers = new Managers
  def managers_=(_managers: Roles.GUIDs) = managers() = _managers

  class BillingManagers extends Roles(this)
  val billingManagers = new BillingManagers
  def billingManagers_=(_billingManagers: Roles.GUIDs) = billingManagers() = _billingManagers

  class Auditors extends Roles(this)
  val auditors = new Auditors
  def auditors_=(_auditors: Roles.GUIDs) = auditors() = _auditors

}
