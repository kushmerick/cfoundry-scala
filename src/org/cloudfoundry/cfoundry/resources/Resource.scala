package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.client._
import scala.collection.mutable._
import scala.language.dynamics
import scala.beans._
import scala.reflect.internal._
import java.util.logging._

class Resource(@BeanProperty var context: ClientContext)
  extends Dynamic with ClassNameUtilities {

  //// constants

  private val id = "id"
  private val NO_ID = None

  private val url = "url"

  //// client context

  private def crud = context.getCrud
  private def inflector = context.getInflector
  private def logger = context.getLogger
  private def token = context.getToken
  private def cache = context.getCache

  //// quick-and-dirty type checking

  def isA(resourceClassBriefName: String) = getBriefClassName == resourceClassBriefName

  //// state

  private val properties = Map[String, Property]()
  private val data: Map[String, Payload] = Map[String, Payload]()

  private val children = Set[String]()
  private val parents = Map[String, Class[_]]()

  private var isDirty: Boolean = false

  // just for java_friendly/Generate 
  def getProperties = properties
  def getChildren = children

  //// most resources have these properties, though subclasses might
  //// declare the property again, for example to override 'source'

  property(id, source = "guid", default = Some(NO_ID), readOnly = true)
  property("name")
  property("description")
  property(url, readOnly = true)

  //// properties (TODO: should be 'static', but can't just move them to
  //// a companion object due to subclassing.)

  protected def property(
    name: String,
    typ: String = "string",
    source: String = null,
    default: Option[Any] = None,
    applicable: Boolean = true,
    readOnly: Boolean = false,
    parental: Boolean = false) = {
    if (applicable) {
      val property = new Property(name, typ, source, default, readOnly, parental)
      properties += name -> property
    } else {
      // TODO: Flesh out the Resource hierarchy to avoid this ugliness
      properties -= name
    }
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
    val default = if (isInstanceOf[ClientContext]) Some(childrenAbsolutePath(children_name)) else None
    property(childrenPathPropertyName(childrenName), source = childrenPathSource(children_name), default = default, readOnly = true)
  }

  private def childrenPathPropertyName(childrenName: String) = {
    // ServiceInstancesUrl; note that it's an absolute path not a URL,
    // but we'll call it a URL because that's what CC calls it 
    childrenName + "Url"
  }

  private def isChildrenPathPropertyName(propertyName: String) = {
    hasProperty(propertyName) &&
      propertyName.endsWith("Url") &&
      hasChildren(propertyName.substring(0, propertyName.length - 3))
  }

  private def childrenPathSource(children_name: String) = {
    // service_instances_url; note that CF sends an absolute path not a URL 
    children_name + "_url"
  }

  private def childrenAbsolutePath(children_name: String) = {
    // /v2/service_instances
    absolutePath(children_name)
  }

  def hasChildren(childrenName: String) = {
    children.contains(childrenName)
  }

  //// parents; ditto.  (TODO: Simplify resource definitions by automatically
  //// generate hasA from hasMany [or the other way around].)

  protected def hasA(parentName: String) = {
    val parentClass = getSiblingClass(inflector.capitalize(parentName))
    parents += parentName -> parentClass
    property(parentName, typ = "resource", parental = true)
    property(parentGuidPropertyName(parentName), parental = true)
  }

  def hasParent(parentName: String) = {
    parents.contains(parentName)
  }

  private def parentGuidPropertyName(parentName: String) = {
    s"${parentName}Guid"
  }

  private def parent_guid_key(parentName: String) = {
    s"${inflector.camelToUnderline(parentName)}_guid"
  }

  def is_parent_guid_key(key: String) = {
    key.endsWith("_guid") &&
      hasProperty(parent_guid_key_to_parentGuidPropertyName(key))
  }

  def parent_guid_key_to_parentGuidPropertyName(key: String) = {
    parentGuidPropertyName(inflector.underlineToCamel(key.substring(0, key.length - 5)))
  }

  //// loading from a cRud response

  def fromPayload(payload: Payload): Unit = {
    fromPayload(payload, Seq("metadata", "entity"))
    cache.touch(this)
    isDirty = false
  }

  private def fromPayload(payload: Payload, keys: Seq[String]) = {
    for (key <- keys) {
      ingest(payload(key))
    }
  }

  private def ingest(raw: Payload) {
    for ((key, value) <- raw.map) {
      val propertyName: String =
        if (hasProperty(key))
          key // service.version
        else
          propertyForSource(key) match {
            case Some(property) => {
              // service.name from key label
              property.name
            }
            case None => {
              if (is_parent_guid_key(key)) {
                // service.servicePlanGuid from key service_plan_guid
                parent_guid_key_to_parentGuidPropertyName(key)
              } else {
                null
              }
            }
          }
      if (propertyName == null) {
        logger.finest(s"Ignoring key '${key}' for resource ${getBriefClassName}")
      } else {
        logger.finest(s"Setting property '${propertyName}' to '${value}' from '${key}' for resource ${getBriefClassName}")
        setData(propertyName, value, sudo = true)
      }
    }
  }

  //// property values

  private def hasData(property: Property): Boolean = hasData(property.name)
  private def hasData(propertyName: String) = data.contains(propertyName)

  protected def setData(property: Property, value: Payload): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Payload, sudo: Boolean): Unit = {
    if (!sudo && property.readOnly) throw new ReadOnly(property, value, this)
    data.put(property.name, value)
  }

  protected def setData(property: Property, value: Any): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Any, sudo: Boolean): Unit = {
    setData(property, Payload(value), sudo)
  }

  protected def setData(propertyName: String, value: Payload): Unit = setData(propertyName, value, false)
  protected def setData(propertyName: String, value: Payload, sudo: Boolean): Unit = {
    if (hasProperty(propertyName)) {
      setData(properties(propertyName), value, sudo)
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  protected def setData(propertyName: String, value: Any): Unit = setData(propertyName, value, false)
  def setData(propertyName: String, value: Any, sudo: Boolean): Unit = {
    setData(propertyName, Payload(value), sudo)
  }

  private def clearData(propertyName: String) = data.remove(propertyName)

  def getData[T](propertyName: String): T = {
    if (hasProperty(propertyName)) {
      getData[T](properties(propertyName))
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  def getData[T](property: Property): T = {
    if (hasProperty(property)) {
      if (hasData(property)) {
        data(property.name).as(property.typ).asInstanceOf[T]
      } else {
        property.default match {
          case Some(value) => value.asInstanceOf[T]
          case None => throw new MissingRequiredProperty(this, property)
        }
      }
    } else {
      throw new InvalidProperty(property.name, this)
    }
  }

  //// model magic via dynamic invocation

  def applyDynamic(noun: String)(args: Any*): Magic = doApplyDynamic(noun, args)

  def doApplyDynamic(noun: String, args: scala.Seq[Any]) = {
    if (args.isEmpty) {
      // client.services()
      // service.servicePlans()
      // service.servicePlan()
      // etc
      selectDynamic(noun)
    } else if (args.length == 1 && args(0).isInstanceOf[Int]) {
      // client.services(3) -- kinda bizarre but we'll interpret as "the 4th service"
      MagicResource(selectDynamic(noun).resources(args(0).asInstanceOf[Int]))
    } else {
      throw new UnexpectedArguments(noun, args)
    }
  }

  def selectDynamic(noun: String) = {
    if (hasProperty(noun)) {
      if (hasParent(noun)) {
        // servicePlan.service
        val parentName = noun
        val pgpn = parentGuidPropertyName(parentName)
        logger.fine(s"Selecting parent '${parentName}' of resource ${this}")
        if (!hasData(parentName)) {
          if (hasData(pgpn)) {
            // lazily load the resource
            val parentGuid = getData[String](pgpn)
            val parent =
              if (cache.contains(parentGuid)) {
            	logger.fine(s"Retrieving resource ${parentGuid} from cache")
                cache.get(parentGuid)
              } else {
                factoryFor(noun).create
              }
            parent.refresh(parentGuid)
            attachParent(parentName, parent, this)
          } else {
            throw new MultipleCauses(
              new MissingRequiredProperty(this, properties(parentName)),
              new MissingRequiredProperty(this, properties(pgpn)))
          }
        }
        MagicResource(getData(noun))
      } else {
        // servicePlan.description
        logger.fine(s"Selecting property '${noun}' of resource ${this}")
        MagicProp(getData(noun))
      }
    } else if (hasChildren(inflector.pluralize(noun))) {
      val factory = factoryFor(noun)
      if (inflector.isSingular(noun)) {
        // servicePlan.serviceInstance
        logger.fine(s"Creating new '${noun}' child of resource ${this}")
        val child = factory.create
        if (!isInstanceOf[ClientResource]) {
          val parentPropertyName = inflector.lowerize(getBriefClassName)
          attachParent(parentPropertyName, this, child)
        }
        MagicResource(child)
      } else {
        // servicePlan.serviceInstances
        logger.fine(s"Enumerating '${noun}' children of resource ${this}")
        val path = getData[String](childrenPathPropertyName(noun))
        MagicResources(enumerate(factory, path))
      }
    } else {
      throw new InvalidProperty(noun, this)
    }
  }

  def updateDynamic(noun: String)(value: Any) = {
    if (hasParent(noun)) {
      var parent: Resource = null
      if (value.isInstanceOf[Resource]) {
        parent = value.asInstanceOf[Resource]
      } else if (value.isInstanceOf[MagicResource]) {
        parent = value.asInstanceOf[Magic].resource
      }
      if (parent == null) {
        throw new InvalidParent(this, noun, value, "not a resource")
      }
      if (!parent.isA(inflector.capitalize(noun))) {
        throw new InvalidParent(this, noun, value, s"not a '${noun}'")
      }
      setData(noun, parent)
      if (parent.hasId) {
        setData(parentGuidPropertyName(noun), parent._getId)
      } else {
        clearData(parentGuidPropertyName(noun))
      }
    } else if (hasProperty(noun)) {
      val oldId = if (noun == id && hasId) _getId else null
      setData(noun, value)
      if (oldId != null) propogateIdToChildren(oldId)
    } else {
      throw new InvalidProperty(noun, this)
    }
    isDirty = true
  }

  private def propogateIdToChildren(oldId: String) = {
    // For example: after changing a Service's GUID, we need to update the
    // ServiceGUID property of ServicePlan that references this Service.
    // Yes, this is slow; but we assume that resource ids rarely change.
    val parentName = inflector.lowerize(getBriefClassName)
    val pgpn = parentGuidPropertyName(parentName)
    val guid = _getId
    for (resource <- cache.getResources) {
      if (resource.hasData(parentName) &&
        resource.hasData(pgpn) &&
        resource.getData[String](pgpn) == oldId) {
        logger.fine(s"Propogating parent ${guid} to child ${resource} of ${this}")
        resource.setData(pgpn, guid)
      }
    }
  }

  //// child creation & enumeration

  private def factoryFor(noun: String) = {
    new Factory(noun, context)
  }

  private def enumerate(factory: Factory, _path: String): Seq[Resource] = {
    var path = _path
    val resources = new ArrayBuilder.ofRef[Resource]
    do {
      val payload = perform(() => crud.cRud(path)(options))
      resources ++= payload("resources").seq.map(resourcePayload => factory.create(resourcePayload))
      path = payload("next_url").string
    } while (path != null)
    resources.result
  }

  //// save = create if resource does not exist on the server, or update if it does

  def save = {
    if (isLocalOnly) {
      create
    } else {
      if (isDirty) update
    }
    cache.touch(this)
  }

  def isLocalOnly = !hasId

  //// destroy

  def destroy = {
    if (hasId) {
      delete
      cache.eject(this)
      clearId
    }
  }

  //// refresh

  def refresh(newId: String = null) = {
    if (newId != null) {
      if (hasId) {
        logger.warning(s"Changing id of ${this} from ${_getId} to ${newId}")
        cache.eject(this)
      }
      setData(id, newId, sudo = true)
    }
    if (hasId) {
      read
      cache.touch(this)
    } else {
      throw new Unrefreshable(this)
    }
  }

  //// property helpers

  private def getUrl = {
    if (hasData(url)) {
      getData[String](url)
    } else if (hasId) {
      if (data.size == 1) {
        // if we just have the id, then we'll assume we're lazily
        // fetching a parent -- ie, this is not a problem
        logger.info(s"Creating URL for ${this}")
      } else {
        logger.warning(s"Generating missing URLfor ${this}")
      }
      s"${absolutePath(inflector.pluralize(inflector.camelToUnderline(getBriefClassName)))}/${_getId}"
    } else {
      getData[String](url) // force exception
    }
  }

  // this need to be public for Cache, so the "_" is to avoid collision
  // with "getId" from java_friendly/Generate 
  def _getId = getData[String](id)

  private def hasId = hasData(id)
  private def clearId = clearData(id)

  def attachParent(parentPropertyName: String, parent: Resource, child: Resource) = {
    if (!parent.isInstanceOf[ClientResource]) {
      child.setData(parentPropertyName, parent)
      child.setData(child.parentGuidPropertyName(parentPropertyName), parent._getId)
    }
  }

  /// absolute paths such as /v2/service_instances

  private def absolutePath: String = {
    absolutePath(inflector.pluralize(inflector.camelToUnderline(getBriefClassName)))
  }

  private def absolutePath(plural_class_name: String = null) = {
    s"/v2/${plural_class_name}"
  }

  ///// CRUD operations

  private def create = {
    val content = Map[String, Any]()
    for ((propertyName, property) <- properties) {
      if (property.readOnly || property.parental) {
        logger.finest(s"Ignoring property '${propertyName}' while creating ${this}")
      } else {
        content.put(property.getTrueSource, getData(property))
      }
    }
    for ((parentName, parentClass) <- parents) {
      val pgpn = parentGuidPropertyName(parentName)
      var parentGuid = if (hasData(pgpn)) getData[String](pgpn) else null
      val parent = if (hasData(parentName)) getData[Resource](parentName) else null
      if (parent == null && parentGuid == null) {
        throw new NotSaveable(this, parentName)
      } else {
        if (parent != null && parentGuid != null && parent._getId != parentGuid) {
          throw new NotSaveable(this, s"Inconsistent parent '${parentName}': '${parentGuid}' and '${parent._getId}'")
        }
        if (parentGuid == null) parentGuid = parent._getId
        content.put(parent_guid_key(parentName), parentGuid)
      }
    }
    val payload = Payload(JSON.serialize(Payload(content)))
    performAndReload(() => crud.Crud(absolutePath)(options)(Some(payload)))
  }

  private def read = {
    performAndReload(() => crud.cRud(getUrl)(options))
  }

  private def update = {
    val metadata = Map(id -> _getId)
    val entity = null // TODO
    val payload = Map("metadata" -> metadata, "entity" -> entity)
    performAndReload(() => crud.crUd(getUrl)(options)(Some(Payload(payload))))
  }

  private def delete = {
    perform(() => crud.cruD(getUrl)(options))
  }

  private def options = {
    Some(Pairs(
      "Authorization" -> token.auth_header))
  }

  private def performAndReload(performer: () => Response) = {
    fromPayload(perform(performer))
  }

  protected def perform(performer: () => Response) = {
    val response = performer()
    if (response.ok) {
      response.payload
    } else {
      throw new BadResponse(response)
    }
  }

  //// just for debugging

  override def toString = {
    val s = new StringBuilder(s"<${getBriefClassName} ")
    var decoration = toStringDecoration
    if (!decoration.isEmpty) {
      s ++= decoration
      s ++= " "
    }
    s ++=
      Payload(
        properties
          .values
          .filter(property => (property.hasDefault || hasData(property)) && !isVerbose(property))
          .map(property => property.name -> getData(property))
          .toMap)
      .toString
    s += '>'
    s.result
  }

  protected def toStringDecoration = {
    val s = new StringBuilder
    if (isDirty) s ++= "D"
    if (isLocalOnly) s ++= "L"
    s.result
  }

  private def isVerbose(property: Property) = {
    property.parental &&
      ({
        val p = parentGuidPropertyName(property.name)
        hasProperty(p) && hasData(p)
      })
  }

}