// DO NOT EDIT -- Automagically generated at 2013-06-06 20:05:28.607 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait OrganizationJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getDescription: java.lang.String = selectDynamic("description").prop.asInstanceOf[java.lang.String]
  def setDescription(value: java.lang.String): Unit = updateDynamic("description")(value)
  def getId: java.lang.String = selectDynamic("id").prop.asInstanceOf[java.lang.String]
  def getName: java.lang.String = selectDynamic("name").prop.asInstanceOf[java.lang.String]
  def setName(value: java.lang.String): Unit = updateDynamic("name")(value)
  def getSpacesUrl: java.lang.String = selectDynamic("spacesUrl").prop.asInstanceOf[java.lang.String]
  def getUrl: java.lang.String = selectDynamic("url").prop.asInstanceOf[java.lang.String]
  def getSpaces: java.util.List[org.cloudfoundry.cfoundry.resources.Space] = selectDynamic("spaces").resources.asInstanceOf[scala.collection.Seq[org.cloudfoundry.cfoundry.resources.Space]]
  def newSpace: org.cloudfoundry.cfoundry.resources.Space = selectDynamic("space").resource.asInstanceOf[org.cloudfoundry.cfoundry.resources.Space]
}
