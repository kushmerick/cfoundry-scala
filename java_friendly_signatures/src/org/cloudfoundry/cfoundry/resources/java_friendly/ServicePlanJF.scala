// DO NOT EDIT -- Automagically generated at 2013-08-14 14:31:49.105 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServicePlanJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getDescription: java.lang.String = selectDynamic("description").raw.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getFree: java.lang.Boolean = selectDynamic("free").raw.asInstanceOf[java.lang.Boolean]
  def setFree(value: java.lang.Boolean): Unit = updateDynamic("free")(value)
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getPublic: java.lang.Boolean = selectDynamic("public").raw.asInstanceOf[java.lang.Boolean]
  def setPublic(value: java.lang.Boolean): Unit = updateDynamic("public")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getService: org.cloudfoundry.cfoundry.resources.Service = selectDynamic("service").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Service]
  def setService(value: org.cloudfoundry.cfoundry.resources.Service): Unit = updateDynamic("service")(value)
  def getServiceGuid: java.lang.String = selectDynamic("serviceGuid").raw.asInstanceOf[java.lang.String]
  def setServiceGuid(value: java.lang.String): Unit = updateDynamic("serviceGuid")(value)
  def getServiceInstancesUrl: java.lang.String = selectDynamic("serviceInstancesUrl").raw.asInstanceOf[java.lang.String]
  def getServiceInstances: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceInstance] = selectDynamic("serviceInstances").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceInstance]]
  def newServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
}
