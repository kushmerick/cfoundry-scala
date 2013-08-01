// DO NOT EDIT -- Automagically generated at 2013-07-31 08:07:22.193 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait AppJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def setSpace(value: org.cloudfoundry.cfoundry.resources.Space): Unit = updateDynamic("space")(value)
  def getSpaceGuid: java.lang.String = selectDynamic("spaceGuid").prop.asInstanceOf[java.lang.String]
  def setSpaceGuid(value: java.lang.String): Unit = updateDynamic("spaceGuid")(value)
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
}
