// DO NOT EDIT -- Automagically generated at 2013-07-12 22:56:16.642 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.client.java_friendly
import scala.collection.JavaConversions._
trait ClientJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getAppsUrl: java.lang.String = selectDynamic("appsUrl").prop.asInstanceOf[java.lang.String]
  def getCfoundryScalaVersion: java.lang.String = selectDynamic("cfoundry_scala_version").prop.asInstanceOf[java.lang.String]
  def getCloudfoundryVersion: java.lang.Integer = selectDynamic("cloudfoundry_version").prop.asInstanceOf[java.lang.Integer]
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getOrganizationsUrl: java.lang.String = selectDynamic("organizationsUrl").prop.asInstanceOf[java.lang.String]
  def getServiceAuthTokensUrl: java.lang.String = selectDynamic("serviceAuthTokensUrl").prop.asInstanceOf[java.lang.String]
  def getServiceBindingsUrl: java.lang.String = selectDynamic("serviceBindingsUrl").prop.asInstanceOf[java.lang.String]
  def getServiceInstancesUrl: java.lang.String = selectDynamic("serviceInstancesUrl").prop.asInstanceOf[java.lang.String]
  def getServicePlansUrl: java.lang.String = selectDynamic("servicePlansUrl").prop.asInstanceOf[java.lang.String]
  def getServicesUrl: java.lang.String = selectDynamic("servicesUrl").prop.asInstanceOf[java.lang.String]
  def getSpacesUrl: java.lang.String = selectDynamic("spacesUrl").prop.asInstanceOf[java.lang.String]
  def getTarget: java.lang.String = selectDynamic("target").prop.asInstanceOf[java.lang.String]
  def getServicePlans: java.util.List[org.cloudfoundry.cfoundry.resources.ServicePlan] = selectDynamic("servicePlans").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServicePlan]]
  def newServicePlan: org.cloudfoundry.cfoundry.resources.ServicePlan = selectDynamic("servicePlan").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServicePlan]
  def getOrganizations: java.util.List[org.cloudfoundry.cfoundry.resources.Organization] = selectDynamic("organizations").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Organization]]
  def newOrganization: org.cloudfoundry.cfoundry.resources.Organization = selectDynamic("organization").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Organization]
  def getServiceBindings: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceBinding] = selectDynamic("serviceBindings").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceBinding]]
  def newServiceBinding: org.cloudfoundry.cfoundry.resources.ServiceBinding = selectDynamic("serviceBinding").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceBinding]
  def getSpaces: java.util.List[org.cloudfoundry.cfoundry.resources.Space] = selectDynamic("spaces").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Space]]
  def newSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def getServices: java.util.List[org.cloudfoundry.cfoundry.resources.Service] = selectDynamic("services").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Service]]
  def newService: org.cloudfoundry.cfoundry.resources.Service = selectDynamic("service").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Service]
  def getServiceAuthTokens: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceAuthToken] = selectDynamic("serviceAuthTokens").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceAuthToken]]
  def newServiceAuthToken: org.cloudfoundry.cfoundry.resources.ServiceAuthToken = selectDynamic("serviceAuthToken").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceAuthToken]
  def getServiceInstances: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceInstance] = selectDynamic("serviceInstances").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceInstance]]
  def newServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
  def getApps: java.util.List[org.cloudfoundry.cfoundry.resources.App] = selectDynamic("apps").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.App]]
  def newApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
}
