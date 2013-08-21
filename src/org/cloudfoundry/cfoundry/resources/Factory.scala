package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.client._
import java.lang.reflect.Constructor

class Factory(val noun: String, val context: ClientContext) extends ClassNameUtilities {

  private def inflector = context.getInflector

  private lazy val plural = inflector.pluralize(noun)

  lazy val resourceClass: Class[_] = {
    getSiblingClass(inflector.capitalize(inflector.singularize(noun)))
  }

  def create = {
    resourceClass.getConstructors()(0).newInstance(context).asInstanceOf[Resource]
  }

  def create(info: Chalice): Resource = {
    val id = info("metadata")("guid").string
    val resource = checkCache(id)
    resource.fromInfo(info)
    resource.clean
    resource
  }
  
  private def checkCache(id: String) = {
    val cache = context.getCache
    val cached = cache.contains(id)
    lazy val rcached = cache.get(id) 
    lazy val dirty = rcached.isDirty
    if (cached && !dirty) {
      context.getLogger.fine(s"Retrieving resource ${id} from cache")
      rcached
    } else {
      if (cached) {
        cache.eject(rcached)
      }
  	  create
    }
  }

}