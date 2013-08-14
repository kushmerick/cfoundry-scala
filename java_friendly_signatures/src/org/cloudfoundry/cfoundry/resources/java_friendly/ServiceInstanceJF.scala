// DO NOT EDIT -- Automagically generated at 2013-08-14 14:31:48.815 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceInstanceJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getServicePlan: org.cloudfoundry.cfoundry.resources.ServicePlan = selectDynamic("servicePlan").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServicePlan]
  def setServicePlan(value: org.cloudfoundry.cfoundry.resources.ServicePlan): Unit = updateDynamic("servicePlan")(value)
  def getServicePlanGuid: java.lang.String = selectDynamic("servicePlanGuid").raw.asInstanceOf[java.lang.String]
  def setServicePlanGuid(value: java.lang.String): Unit = updateDynamic("servicePlanGuid")(value)
  def getSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def setSpace(value: org.cloudfoundry.cfoundry.resources.Space): Unit = updateDynamic("space")(value)
  def getSpaceGuid: java.lang.String = selectDynamic("spaceGuid").raw.asInstanceOf[java.lang.String]
  def setSpaceGuid(value: java.lang.String): Unit = updateDynamic("spaceGuid")(value)
}
