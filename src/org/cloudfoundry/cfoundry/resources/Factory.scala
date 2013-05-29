package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.client._
import java.lang.reflect.Constructor

class Factory(noun: String, context: ClientContext) extends ClassNameUtilities {

  private def inflector = context.getInflector

  lazy val plural = inflector.pluralize(noun)

  private lazy val resourceClass: Class[_] = {
    getSiblingClass(inflector.capitalize(inflector.singularize(noun)))
  }

  lazy val create: Resource = {
    resourceClass.getConstructors()(0).newInstance(context).asInstanceOf[Resource]
  }

  def create(payload: Payload): Resource = {
    create.fromPayload(payload)
  }

}