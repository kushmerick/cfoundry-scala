// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:12.695 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.client.java_friendly
import scala.collection.JavaConversions._
trait ClientJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getAppsUrl: java.lang.String = selectDynamic("appsUrl").raw.asInstanceOf[java.lang.String]
  def getCloudfoundryVersion: java.lang.Integer = selectDynamic("cloudfoundryVersion").raw.asInstanceOf[java.lang.Integer]
  def getCurrentUser: org.cloudfoundry.cfoundry.resources.Resource = selectDynamic("currentUser").raw.asInstanceOf[org.cloudfoundry.cfoundry.resources.Resource]
  def getDescription: java.lang.String = selectDynamic("description").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def getOrganizationsUrl: java.lang.String = selectDynamic("organizationsUrl").raw.asInstanceOf[java.lang.String]
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getServiceAuthTokensUrl: java.lang.String = selectDynamic("serviceAuthTokensUrl").raw.asInstanceOf[java.lang.String]
  def getServiceBindingsUrl: java.lang.String = selectDynamic("serviceBindingsUrl").raw.asInstanceOf[java.lang.String]
  def getServiceInstancesUrl: java.lang.String = selectDynamic("serviceInstancesUrl").raw.asInstanceOf[java.lang.String]
  def getServicePlansUrl: java.lang.String = selectDynamic("servicePlansUrl").raw.asInstanceOf[java.lang.String]
  def getServicesUrl: java.lang.String = selectDynamic("servicesUrl").raw.asInstanceOf[java.lang.String]
  def getSpacesUrl: java.lang.String = selectDynamic("spacesUrl").raw.asInstanceOf[java.lang.String]
  def getTarget: java.lang.String = selectDynamic("target").raw.asInstanceOf[java.lang.String]
  def getUrl: java.lang.String = selectDynamic("url").raw.asInstanceOf[java.lang.String]
  def setUrl(value: java.lang.String): Unit = updateDynamic("url")(value)
  def getUsersUrl: java.lang.String = selectDynamic("usersUrl").raw.asInstanceOf[java.lang.String]
  def getVersion: java.lang.String = selectDynamic("version").raw.asInstanceOf[java.lang.String]
  def getServicePlans: java.util.List[org.cloudfoundry.cfoundry.resources.ServicePlan] = selectDynamic("servicePlans").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServicePlan]]
  def newServicePlan: org.cloudfoundry.cfoundry.resources.ServicePlan = selectDynamic("servicePlan").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServicePlan]
  def getOrganizations: java.util.List[org.cloudfoundry.cfoundry.resources.Organization] = selectDynamic("organizations").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Organization]]
  def newOrganization: org.cloudfoundry.cfoundry.resources.Organization = selectDynamic("organization").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Organization]
  def getServiceBindings: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceBinding] = selectDynamic("serviceBindings").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceBinding]]
  def newServiceBinding: org.cloudfoundry.cfoundry.resources.ServiceBinding = selectDynamic("serviceBinding").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceBinding]
  def getSpaces: java.util.List[org.cloudfoundry.cfoundry.resources.Space] = selectDynamic("spaces").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Space]]
  def newSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def getUsers: java.util.List[org.cloudfoundry.cfoundry.resources.User] = selectDynamic("users").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.User]]
  def newUser: org.cloudfoundry.cfoundry.resources.User = selectDynamic("user").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.User]
  def getServices: java.util.List[org.cloudfoundry.cfoundry.resources.Service] = selectDynamic("services").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Service]]
  def newService: org.cloudfoundry.cfoundry.resources.Service = selectDynamic("service").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Service]
  def getServiceAuthTokens: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceAuthToken] = selectDynamic("serviceAuthTokens").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceAuthToken]]
  def newServiceAuthToken: org.cloudfoundry.cfoundry.resources.ServiceAuthToken = selectDynamic("serviceAuthToken").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceAuthToken]
  def getServiceInstances: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceInstance] = selectDynamic("serviceInstances").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceInstance]]
  def newServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
  def getApps: java.util.List[org.cloudfoundry.cfoundry.resources.App] = selectDynamic("apps").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.App]]
  def newApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
}
