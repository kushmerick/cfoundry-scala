// DO NOT EDIT -- Automagically generated at 2013-08-14 14:31:47.98 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getActive: java.lang.Boolean = selectDynamic("active").raw.asInstanceOf[java.lang.Boolean]
  def setActive(value: java.lang.Boolean): Unit = updateDynamic("active")(value)
  def getDescription: java.lang.String = selectDynamic("description").raw.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getGatewayUrl: java.lang.String = selectDynamic("gatewayUrl").raw.asInstanceOf[java.lang.String]
  def setGatewayUrl(value: java.lang.String): Unit = updateDynamic("gatewayUrl")(value)
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getProvider: java.lang.String = selectDynamic("provider").raw.asInstanceOf[java.lang.String]
  def setProvider(value: java.lang.String): Unit = updateDynamic("provider")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getServicePlansUrl: java.lang.String = selectDynamic("servicePlansUrl").raw.asInstanceOf[java.lang.String]
  def getVersion: java.lang.String = selectDynamic("version").raw.asInstanceOf[java.lang.String]
  def setVersion(value: java.lang.String): Unit = updateDynamic("version")(value)
  def getServicePlans: java.util.List[org.cloudfoundry.cfoundry.resources.ServicePlan] = selectDynamic("servicePlans").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServicePlan]]
  def newServicePlan: org.cloudfoundry.cfoundry.resources.ServicePlan = selectDynamic("servicePlan").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServicePlan]
}
