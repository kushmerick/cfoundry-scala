// DO NOT EDIT -- Automagically generated at 2013-07-12 22:56:12.461 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getActive: java.lang.Boolean = selectDynamic("active").prop.asInstanceOf[java.lang.Boolean]
  def setActive(value: java.lang.Boolean): Unit = updateDynamic("active")(value)
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getProvider: java.lang.String = selectDynamic("provider").prop.asInstanceOf[java.lang.String]
  def setProvider(value: java.lang.String): Unit = updateDynamic("provider")(value)
  def getServicePlansUrl: java.lang.String = selectDynamic("servicePlansUrl").prop.asInstanceOf[java.lang.String]
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
  def getVersion: java.lang.String = selectDynamic("version").prop.asInstanceOf[java.lang.String]
  def setVersion(value: java.lang.String): Unit = updateDynamic("version")(value)
  def getServicePlans: java.util.List[org.cloudfoundry.cfoundry.resources.ServicePlan] = selectDynamic("servicePlans").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServicePlan]]
  def newServicePlan: org.cloudfoundry.cfoundry.resources.ServicePlan = selectDynamic("servicePlan").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServicePlan]
}
