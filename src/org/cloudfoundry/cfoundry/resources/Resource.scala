package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.client._
import scala.collection.mutable._
import scala.language.dynamics
import scala.beans._
import java.util.logging._

class Resource(@BeanProperty var client: ClientContext)
  extends Dynamic with ClassNameUtilities {

  //// constants

  private val id = "id"
  private val url = "url"
  private val LOCAL_RESOURCE_SENTINEL = "LOCAL_RESOURCE_SENTINEL"

  //// client context

  private def crud = client.getCrud
  private def inflector = client.getInflector
  private def logger = client.getLogger
  private def token = client.getToken

  //// state

  private val properties = Map[String, Property]()
  private val data: Map[String, Payload] = Map[String, Payload]()

  private val children = Set[String]()
  private val parents = Set[String]()

  private var isDirty: Boolean = false

  //// most resources have these properties, though subclasses might
  //// declare the property again, for example to override 'source'

  property(id, source = "guid", default = Some(LOCAL_RESOURCE_SENTINEL))
  property("name")
  property("description")
  property(url)

  //// properties (TODO: should be 'static', but can't just move them to
  //// a companion object due to subclassing.)

  protected def property(name: String, typ: String = "string", source: String = null, filter: Filter = null, default: Option[Any] = None, applicable: Boolean = true) = {
    if (applicable)
      properties += name -> new Property(name, typ, source, filter, default)
    else
      properties -= name
  }

  private def hasProperty(name: String) = properties.contains(name)
  private def hasProperty(property: Property): Boolean = hasProperty(property.name)

  private def propertyForSource(source: String) = {
    properties.values.find(property => source == property.source)
  }

  //// children (ditto)

  protected def hasMany(childName: String) = {
    val childrenName = inflector.pluralize(childName)
    children += childrenName
    val children_name = inflector.camelToUnderline(childrenName)
    val absolutePath = if (isInstanceOf[ClientContext]) Some(childrenAbsolutePath(children_name)) else None
    property(childrenPathPropertyName(childrenName), source = childrenPathSource(children_name), default = absolutePath)
  }

  private def childrenPathPropertyName(childrenName: String) = {
    // ServiceInstancesURL; note that it's an absolute path not a URL,
    // but we'll use "URL" because CC calls it a "URL"
    childrenName + "URL"
  }

  private def childrenPathSource(children_name: String) = {
    // service_instances_url; note that CF sends an absolute path not a URL 
    children_name + "_url"
  }

  private def childrenAbsolutePath(children_name: String) = {
    // /v2/service_instances
    absolutePath(children_name)
  }

  protected def hasChildren(childrenName: String) = {
    children.contains(childrenName)
  }

  //// parents (ditto)

  protected def hasA(parentName: String) = {
    parents += parentName
  }

  //// loading from a cRud response

  def fromPayload(payload: Payload): Resource = {
    fromPayload(payload, Seq("metadata", "entity"))
  }

  private def fromPayload(payload: Payload, keys: Seq[String]) = {
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
        setData(propertyName, value)
    }
  }

  //// save = create if resource does not exist on the server, or update if it does

  def save = {
    if (isLocalOnly) {
      Crud
    } else if (isDirty) {
      crUd
    }
    isDirty = false
  }

  def isLocalOnly = {
    getData[String]("id") == LOCAL_RESOURCE_SENTINEL
  }

  //// delete

  def delete = {
    if (!isLocalOnly) cruD
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

  def setData(propertyName: String, value: Payload) = {
    data.put(propertyName, value)
  }

  //// model magic via dynamic invocation

  def applyDynamic(noun: String)(args: Any*) = {
    if (args.isEmpty) {
      // client.services()
      // service.servicePlans()
      // etc
      selectDynamic(noun)
    } else {
      // why do we end up here not magic?
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
          MagicResource(factory.create)
        } else {
          // service.servicePlans
          // TODO: Link children & parent
          logger.fine(s"Enumerating '${noun}' children of resource ${this}")
          val path = getData[String](childrenPathPropertyName(noun))
          MagicResources(enumerate(factory, path))
        }
      } else {
        throw new InvalidProperty(noun, this)
      }
    }
  }

  def updateDynamic(noun: String)(value: Any) = {
    // TODO
  }

  //// sugar for Java

  def o(noun: String): Magic = {
    selectDynamic(noun)
  }

  //// child creation & enumeration

  private def factoryFor(noun: String) = {
    new Factory(noun, client)
  }

  private def enumerate(factory: Factory, _path: String): Seq[Resource] = {
    var path = _path
    val resources = new ArrayBuilder.ofRef[Resource]
    do {
      val payload = perform(() => crud.read(path)(options))
      resources ++= payload("resources").seq.map(resourcePayload => factory.create(resourcePayload))
      path = payload("next_url").string
    } while (path != null)
    resources.result
  }

  //// absolute paths such as /v2/service_instances

  private def absolutePath(): String = {
    absolutePath(inflector.pluralize(inflector.camelToUnderline(getBriefClassName)))
  }

  private def absolutePath(plural_class_name: String = null) = {
    s"/v2/${plural_class_name}"
  }

  ///// CRUD operations

  private def Crud = {
    // TODO
    val payload: Payload = null
    perform(() => crud.create(absolutePath)(options)(Some(payload)))
  }

  private def cRud = {
    perform(() => crud.read(getUrl)(options))
  }

  private def crUd = {
    // TODO
    val payload: Payload = null
    perform(() => crud.update(getUrl)(options)(Some(payload)))
  }

  private def cruD = {
    perform(() => crud.delete(getUrl)(options))
  }

  private def getUrl = getData[String](url)

  private def options = {
    Some(Pairs(
      "Authorization" -> token.auth_header))
  }

  private def perform(performer: () => Response) = {
    val response = performer()
    if (response.ok)
      response.payload
    else
      throw new BadResponse(response)
  }

  //// just for debugging

  override def toString = {
    val s = new StringBuilder(s"<${getBriefClassName} ")
    s ++=
      Payload(
        properties
          .values
          .filter(property => property.hasDefault || hasData(property))
          .map(property => (property.name -> getData(property)))
          .toMap)
      .toString
    s += '>'
    s.result
  }

}
