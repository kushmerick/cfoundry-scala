package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import java.util.logging._

class Factory(noun: String, crud: CRUD, tokenProvider: TokenProvider, inflector: Inflector, logger: Logger) {

  lazy val plural = inflector.pluralize(noun)
  private lazy val singular = inflector.singularize(noun)
  private lazy val Singular = inflector.capitalize(singular)

  private def resourceClass: Class[_] = {
    Class.forName(s"${PACKAGE_NAME}.${Singular}")
  }

  def create: Resource = {
    val resource = resourceClass.newInstance.asInstanceOf[Resource]
    resource.crud = crud
    resource.tokenProvider = tokenProvider
    resource.logger = logger
    resource
  }

  def create(payload: Payload): Resource = {
    create.fromPayload(payload)
  }

  private lazy val PACKAGE_NAME = getClass.getPackage.getName

}