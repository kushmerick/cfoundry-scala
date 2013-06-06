// DO NOT EDIT -- Automagically generated at 2013-06-05 17:04:22.518 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServicePlanJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getService: org.cloudfoundry.cfoundry.resources.Service = selectDynamic("service").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Service]
  def setService(value: org.cloudfoundry.cfoundry.resources.Service): Unit = updateDynamic("service")(value)
  def getServiceGuid: java.lang.String = selectDynamic("serviceGuid").prop.asInstanceOf[java.lang.String]
  def setServiceGuid(value: java.lang.String): Unit = updateDynamic("serviceGuid")(value)
  def getServiceInstancesUrl: java.lang.String = selectDynamic("serviceInstancesUrl").prop.asInstanceOf[java.lang.String]
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
  def getServiceInstances: java.util.List[org.cloudfoundry.cfoundry.resources.ServiceInstance] = selectDynamic("serviceInstances").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.ServiceInstance]]
  def newServiceInstance: org.cloudfoundry.cfoundry.resources.ServiceInstance = selectDynamic("serviceInstance").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.ServiceInstance]
}
