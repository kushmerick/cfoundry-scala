// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:12.196 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait UserJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getAdmin: java.lang.Boolean = selectDynamic("admin").raw.asInstanceOf[java.lang.Boolean]
  def setAdmin(value: java.lang.Boolean): Unit = updateDynamic("admin")(value)
  def getId: java.lang.String = selectDynamic("id").raw.asInstanceOf[java.lang.String]
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
}
