// DO NOT EDIT -- Automagically generated at 2013-07-12 14:56:26.449 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceBindingJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getApp: org.cloudfoundry.cfoundry.resources.App = selectDynamic("app").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.App]
  def setApp(value: org.cloudfoundry.cfoundry.resources.App): Unit = updateDynamic("app")(value)
  def getAppGuid: java.lang.String = selectDynamic("appGuid").prop.asInstanceOf[java.lang.String]
  def setAppGuid(value: java.lang.String): Unit = updateDynamic("appGuid")(value)
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
  def setServiceInstance(value: org.cloudfoundry.cfoundry.resources.ServiceInstance): Unit = updateDynamic("serviceInstance")(value)
  def getServiceInstanceGuid: java.lang.String = selectDynamic("serviceInstanceGuid").prop.asInstanceOf[java.lang.String]
  def setServiceInstanceGuid(value: java.lang.String): Unit = updateDynamic("serviceInstanceGuid")(value)
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
}
