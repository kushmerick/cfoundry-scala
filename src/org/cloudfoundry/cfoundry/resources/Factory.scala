package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.util._

class Factory(val singular: String) {

  import Factory._
  import Inflector._

  val plural = pluralize(singular)
  private val Singular = capitalize(singular)

  private def resourceClass: Class[_] = {
    Class.forName(s"${PACKAGE_NAME}.${Singular}")
  }

  def apply(magician: Magician): Resource = {
    val resource = resourceClass.newInstance.asInstanceOf[Resource]
    resource.magician = magician
    resource
  }

  def apply(magician: Magician, payload: Payload): Resource = {
    apply(magician).fromPayload(payload)
  }

}

object Factory {

  private val PACKAGE_NAME = getClass.getPackage.getName

}