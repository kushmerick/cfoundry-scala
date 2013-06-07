// DO NOT EDIT -- Automagically generated at 2013-06-06 15:24:03.989 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait SpaceJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getAppsUrl: java.lang.String = selectDynamic("appsUrl").prop.asInstanceOf[java.lang.String]
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getOrganization: org.cloudfoundry.cfoundry.resources.Organization = selectDynamic("organization").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Organization]
  def setOrganization(value: org.cloudfoundry.cfoundry.resources.Organization): Unit = updateDynamic("organization")(value)
  def getOrganizationGuid: java.lang.String = selectDynamic("organizationGuid").prop.asInstanceOf[java.lang.String]
  def setOrganizationGuid(value: java.lang.String): Unit = updateDynamic("organizationGuid")(value)
  def getServiceInstancesUrl: java.lang.String = selectDynamic("serviceInstancesUrl").prop.asInstanceOf[java.lang.String]
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
  def getServiceInstances: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceInstance] = selectDynamic("serviceInstances").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceInstance]]
  def newServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
  def getApps: java.util.List[org.cloudfoundry.cfoundry.resources.App] = selectDynamic("apps").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.App]]
  def newApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
}
