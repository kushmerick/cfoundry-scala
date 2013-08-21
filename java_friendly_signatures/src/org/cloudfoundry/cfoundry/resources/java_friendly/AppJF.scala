// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:07.953 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait AppJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
  def setSpace(value: org.cloudfoundry.cfoundry.resources.Space): Unit = updateDynamic("space")(value)
  def getSpaceGuid: java.lang.String = selectDynamic("spaceGuid").raw.asInstanceOf[java.lang.String]
  def setSpaceGuid(value: java.lang.String): Unit = updateDynamic("spaceGuid")(value)
}
