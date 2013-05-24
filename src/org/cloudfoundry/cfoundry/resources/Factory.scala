package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.client._
import java.lang.reflect.Constructor

class Factory(noun: String, client: ClientContext) {

  private def inflector = client.getInflector

  def plural = inflector.pluralize(noun)

  private def resourceClass: Class[_] = {
    val Singular = inflector.capitalize(inflector.singularize(noun))
    val packageName = getClass.getPackage.getName
    Class.forName(s"${packageName}.${Singular}")
  }

  def create: Resource = {
    resourceClass.getConstructors()(0).newInstance(client).asInstanceOf[Resource]
  }

  def create(payload: Payload): Resource = {
    create.fromPayload(payload)
  }

}