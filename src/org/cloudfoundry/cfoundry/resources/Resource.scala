package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import scala.collection.mutable._
import scala.language.dynamics
import java.util.logging._

class Resource extends Dynamic with ClassNameUtilities with TokenProvider {

  //// state

  val inflector = new Inflector

  var logger: Logger = null

  // than make these global, these are set at the root by Client, then propogated
  // to all children by Factory.create.  is this hacky or elegant?
  var crud: CRUD = null
  var tokenProvider: TokenProvider = null

  private val properties = Map[String, Property]()
  private val children = Map[String, Class[_]]() // TODO: We never actually use the class?!

  private var data: Map[String, Payload] = Map[String, Payload]()

  //// every resource has these properties, though subclasses might
  //// declare the property again, for example to override 'source'

  property("id", source = "guid")
  property("name")
  property("description")

  //// properties (TODO: should be 'static', but can't just move them to
  //// a companion object due to subclassing.)

  protected def property(name: String, typ: String = "string", source: String = null, filter: Filter = null, default: Option[Any] = None) = {
    properties += name -> new Property(name, typ, source, filter, default)
  }

  private def hasProperty(name: String) = properties.contains(name)
  private def hasProperty(property: Property): Boolean = hasProperty(property.name)

  private def propertyForSource(source: String) = {
    properties.values.find(property => source == property.source)
  }

  //// children (ditto)

  protected def one_to_many(childName: String, root: Boolean = false) = {
    val childClassName = inflector.capitalize(childName)
    val childClass = getClass(classOf[Resource], childClassName)
    val childrenName = inflector.pluralize(childName)
    children += childrenName -> childClass
    val rootUrl = if (root) childrenRootUrl(childrenName) else null
    property(childrenUrlPropertyName(childrenName), source = childrenUrlSource(childrenName), default = Some(rootUrl))
  }

  private def childrenUrlPropertyName(childrenName: String) = {
    childrenName + "URL"
  }

  private def childrenUrlSource(childrenName: String) = {
    inflector.camelToUnderline(childrenName) + "_url"
  }

  private def childrenRootUrl(childrenName: String) = {
    API_PREFIX + '/' + inflector.camelToUnderline(childrenName)
  }

  private def hasChildren(childrenName: String) = {
    children.contains(childrenName)
  }

  //// loading from a cRud response

  def fromPayload(payload: Payload): Resource = {
    fromPayload(payload, METADATA, ENTITY)
  }

  private def fromPayload(payload: Payload, keys: String*) = {
    for (key <- keys) {
      ingest(payload(key))
    }
    this
  }

  private def ingest(raw: Payload) {
    for ((key, value) <- raw.map) {
      val propertyName: String =
        if (hasProperty(key))
          key
        else
          propertyForSource(key) match { case Some(property) => property.name; case None => null }
      if (propertyName != null)
        data.put(propertyName, value)
    }
  }

  //// property values

  private def hasData(property: Property): Boolean = hasData(property.name)
  private def hasData(propertyName: String) = data.contains(propertyName)

  def getData[T](propertyName: String): T = {
    if (hasProperty(propertyName)) {
      getData(properties(propertyName)).asInstanceOf[T]
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  def getData(property: Property) = {
    if (hasProperty(property)) {
      if (hasData(property)) {
        data(property.name).as(property.typ)
      } else {
        property.default match {
          case Some(value) => value
          case None => throw new MissingRequiredProperty(this, property)
        }
      }
    } else {
      throw new InvalidProperty(property.name, this)
    }
  }

  //// model magic via dynamic invocation

  def applyDynamic(noun: String)(args: Any*) = {
    if (args.isEmpty) {
      // client.services()
      // service.servicePlans()
      // etc
      selectDynamic(noun)
    } else {
      throw new UnexpectedArguments(noun, args)
    }
  }

  def selectDynamic(noun: String) = {
    if (hasProperty(noun)) {
      // service.version
      logger.fine(s"Selecting property '${noun}' of resource ${this}")
      MagicProp(getData(noun))
    } else {
      if (hasChildren(inflector.pluralize(noun))) {
        val factory = factoryFor(noun)
        if (inflector.isSingular(noun)) {
          // service.servicePlan
          logger.fine(s"Creating a new '${noun}' child of resource ${this}")
          // TODO: Link child & parent
          MagicResource(create(factory))
        } else {
          // service.servicePlans
          logger.fine(s"Enumerating '${noun}' children of resource ${this}")
          val path = getData[String](childrenUrlPropertyName(noun))
          // TODO: Link children & parent
          MagicResources(enumerate(factory, path))
        }
      } else {
        throw new InvalidProperty(noun, this)
      }
    }
  }

  def updateDynamic(noun: String, value: Any) = {
    // TODO
  }

  //// sugar for Java

  def o(noun: String): Magic = {
    selectDynamic(noun)
  }

  //// child creation & enumeration

  private def factoryFor(noun: String) = {
    new Factory(noun, crud, tokenProvider, inflector, logger)
  }

  private def create(factory: Factory) = {
    factory.create
  }

  private def enumerate(factory: Factory, _path: String): Seq[Resource] = {
    var path = _path
    val resources = new ArrayBuilder.ofRef[Resource]
    do {
      val payload = read(path)
      resources ++= payload(RESOURCES).seq.map(resourcePayload => factory.create(resourcePayload))
      path = payload(NEXT_URL).string
    } while (path != null)
    resources.result
  }

  ///// CRUD operations

  private def read(path: String) = {
    var path2 = Buffer(path)
    val response = crud.read(path2)(options)
    if (response.ok)
      response.payload
    else
      throw new BadResponse(response)
  }

  private def options = {
    Some(Pairs(
      "Authorization" -> tokenProvider.getToken.auth_header))
  }

  //// constants

  private val RESOURCES = "resources"
  private val METADATA = "metadata"
  private val ENTITY = "entity"
  private val ID = "id"
  private val API_PREFIX = "v2"
  private val NEXT_URL = "next_url"

  //// just for debugging

  override def toString = {
    val s = new StringBuilder(s"<${getBriefClassName} ")
    s ++=
      Payload(
        properties
          .values
          .filter(property => hasData(property))
          .map(property => (property.name -> getData(property)))
          .toMap)
      .toString
    s += '>'
    s.result
  }

}
