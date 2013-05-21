package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import scala.reflect.runtime.universe
import scala.collection.mutable.ArrayBuilder
import scala.collection.mutable.Buffer

class Magician(crud: CRUD, tokenProvider: TokenProvider) {

  import Magician._
  import Inflector._

  // client.services
  // client.service
  // client.service.name = 'foo'

  def apply(noun: String, args: Seq[Any]): Any = {
    if (args.isEmpty) {
      select(noun)
    } else {
      throw new UnexpectedArguments(noun, args)
    }
  }

  def select(noun: String): Magic = {
    val factory = factoryFor(noun)
    if (isSingular(noun))
      // client.service
      MagicResource(create(factory))
    else
      // client.services
      MagicResources(enumerate(factory))
  }

  def update(method: String, value: Any): Any = {
  }

  /////////////////

  def selectForResource(resource: Resource, noun: String) = {
    if (resource.hasProperty(noun)) {
      // service.version
      MagicProp(resource.getData(noun))
    } else {
      val singular = Inflector.singularize(noun)
      if (resource.hasChild(singular)) {
        if (Inflector.isSingular(noun)) {
          // service.servicePlan
          MagicResource(create(factoryFor(noun)))
        } else {
          // service.servicePlans
          val path = resource.getData(resource.childUrlPropertyName(noun))
          MagicResources(enumerateChildren(noun, path))
        }
      } else {
        throw new InvalidProperty(noun, resource)
      }
    }
  }

  /////////////////

  def enumerateChildren(childResourceName: String, childResourcePath: String) = {
    enumerate(factoryFor(childResourceName), childResourcePath, true)
  }

  /////////////////

  private def factoryFor(noun: String) = {
    new Factory(singularize(noun))
  }

  /////////////////

  private def create(factory: Factory) = {
    factory(this)
  }

  private def enumerate(factory: Factory): Seq[Resource] = {
    enumerate(factory, factory.plural)
  }

  private def enumerate(factory: Factory, _path: String, _absolute: Boolean = false): Seq[Resource] = {
    var path = _path
    var absolute = _absolute
    val resources = new ArrayBuilder.ofRef[Resource]
    do {
      val payload = read(path, absolute)
      resources ++= payload(RESOURCES).seq.map(x => factory(this, x))
      path = payload(NEXT_URL).string
      absolute = true
    } while (path != null)
    resources.result
  }

  // convert paths specified by strings or string sequences
  // to the PathComponent expected by CRUD
  private implicit def s2c(s: String) = Left(s)
  private implicit def sseq2c(sseq: Seq[String]) = Right(sseq)

  private def read(path: String, absolute: Boolean) = {
    var path2 = Buffer(path)
    if (!absolute) path2.+=:(API_PREFIX)
    val response = crud.read(path2)(options)
    if (response.ok)
      response.payload
    else
      throw new BadResponse(response)
  }

  private def options = {
    Some(Pairs(
      "Authorization" -> tokenProvider.token.auth_header))
  }

}

object Magician {

  private val API_PREFIX = "v2"
  private val RESOURCES = "resources"
  private val NEXT_URL = "next_url"

}