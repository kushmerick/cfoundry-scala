// DO NOT EDIT -- Automagically generated at 2013-08-21 10:06:10.792 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceAuthTokenJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.util.Chalice
  def updateDynamic(noun: String)(value: Any): Unit
  def getLabel: java.lang.String = selectDynamic("label").raw.asInstanceOf[java.lang.String]
  def setLabel(value: java.lang.String): Unit = updateDynamic("label")(value)
  def getProvider: java.lang.String = selectDynamic("provider").raw.asInstanceOf[java.lang.String]
  def setProvider(value: java.lang.String): Unit = updateDynamic("provider")(value)
  def getResourceUrl: java.lang.String = selectDynamic("resourceUrl").raw.asInstanceOf[java.lang.String]
  def getToken: java.lang.String = selectDynamic("token").raw.asInstanceOf[java.lang.String]
  def setToken(value: java.lang.String): Unit = updateDynamic("token")(value)
}
