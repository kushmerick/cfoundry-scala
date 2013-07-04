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

  def create: Resource = {
    resourceClass.getConstructors()(0).newInstance(context).asInstanceOf[Resource]
  }

  def create(info: Chalice): Resource = {
    val id = info("metadata")("guid").string
    val cache = context.getCache
    val resource: Resource =
      if (cache.contains(id)) {
        context.getLogger.fine(s"Retrieving resource ${id} from cache")
        cache.get(id)
      } else {
        create
      }
    resource.fromInfo(info)
    resource
  }

}