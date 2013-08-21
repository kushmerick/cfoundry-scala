// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:09.7 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait OrganizationJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").raw.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getSpacesUrl: java.lang.String = selectDynamic("spacesUrl").raw.asInstanceOf[java.lang.String]
  def getSpaces: java.util.List[org.cloudfoundry.cfoundry.resources.Space] = selectDynamic("spaces").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Space]]
  def newSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
}
