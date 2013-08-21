// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:11.057 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceBindingJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
  def setApp(value: org.cloudfoundry.cfoundry.resources.App): Unit = updateDynamic("app")(value)
  def getAppGuid: java.lang.String = selectDynamic("appGuid").raw.asInstanceOf[java.lang.String]
  def setAppGuid(value: java.lang.String): Unit = updateDynamic("appGuid")(value)
  def getDescription: java.lang.String = selectDynamic("description").raw.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
  def setServiceInstance(value: org.cloudfoundry.cfoundry.resources.ServiceInstance): Unit = updateDynamic("serviceInstance")(value)
  def getServiceInstanceGuid: java.lang.String = selectDynamic("serviceInstanceGuid").raw.asInstanceOf[java.lang.String]
  def setServiceInstanceGuid(value: java.lang.String): Unit = updateDynamic("serviceInstanceGuid")(value)
}
