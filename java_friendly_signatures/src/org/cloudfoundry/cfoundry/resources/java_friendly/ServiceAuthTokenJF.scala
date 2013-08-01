// DO NOT EDIT -- Automagically generated at 2013-07-31 08:07:25.019 by org.cloudfoundry.cfoundry.java_friendly.Generate$
package org.cloudfoundry.cfoundry.resources.java_friendly
import scala.collection.JavaConversions._
trait ServiceAuthTokenJF {
  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic
  def updateDynamic(noun: String)(value: Any): Unit
  def getLabel: java.lang.String = selectDynamic("label").prop.asInstanceOf[java.lang.String]
  def setLabel(value: java.lang.String): Unit = updateDynamic("label")(value)
  def getProvider: java.lang.String = selectDynamic("provider").prop.asInstanceOf[java.lang.String]
  def setProvider(value: java.lang.String): Unit = updateDynamic("provider")(value)
  def getToken: java.lang.String = selectDynamic("token").prop.asInstanceOf[java.lang.String]
  def setToken(value: java.lang.String): Unit = updateDynamic("token")(value)
}
