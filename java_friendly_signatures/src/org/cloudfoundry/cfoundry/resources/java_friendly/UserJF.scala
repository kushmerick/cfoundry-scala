// DO NOT EDIT -- Automagically generated at 2013-08-14 14:31:49.851 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait UserJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getAdmin: java.lang.Boolean = selectDynamic("admin").raw.asInstanceOf[java.lang.Boolean]
  def setAdmin(value: java.lang.Boolean): Unit = updateDynamic("admin")(value)
  def getAppsUrl: java.lang.String = selectDynamic("appsUrl").raw.asInstanceOf[java.lang.String]
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getOrganizationsUrl: java.lang.String = selectDynamic("organizationsUrl").raw.asInstanceOf[java.lang.String]
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getSpacesUrl: java.lang.String = selectDynamic("spacesUrl").raw.asInstanceOf[java.lang.String]
  def getOrganizations: java.util.List[org.cloudfoundry.cfoundry.resources.Organization] = selectDynamic("organizations").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Organization]]
  def newOrganization: org.cloudfoundry.cfoundry.resources.Organization = selectDynamic("organization").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Organization]
  def getSpaces: java.util.List[org.cloudfoundry.cfoundry.resources.Space] = selectDynamic("spaces").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Space]]
  def newSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def getApps: java.util.List[org.cloudfoundry.cfoundry.resources.App] = selectDynamic("apps").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.App]]
  def newApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
}
