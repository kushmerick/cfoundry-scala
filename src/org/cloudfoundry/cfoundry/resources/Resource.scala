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
import org.apache.http._

abstract class Resource(@BeanProperty var context: ClientContext)
  extends Dynamic with ClassNameUtilities {

  //// constants

  private val id = "id"
  private val NOID = Some(None)

  private val url = "resourceUrl"

  //// client context

  private def crud = context.getCrud
  private def inflector = context.getInflector
  private def logger = context.getLogger
  private def token = context.getToken
  private def cache = context.getCache
  private def authenticator = context.getAuthenticator

  //// quick-and-dirty type checking

  private def isA(resourceClassBriefName: String) = getBriefClassName == resourceClassBriefName

  //// state

  val properties = Map[String, Property]()
  private val data = Map[String, Chalice]()

  private val children = Set[String]()
  val parents = Map[String, Class[_]]()

  private var dirty = Set[Property]()
  def isDirty = dirty.nonEmpty
  def clean = dirty.clear

  // just for java_friendly/Generate 
  def getProperties = properties
  def getChildren = children

  //// most resources have these properties, though subclasses might
  //// declare the property again, for example to override 'source'

  property(id, source = "guid", default = NOID, readOnly = true, metadata = true)
  property(url, source = "url", readOnly = true, metadata = true)
  property("name")
  property("description")

  //// properties

  protected def property(
    name: String,
    typ: String = "string",
    source: String = null,
    default: Option[Any] = None,
    applicable: Boolean = true,
    readOnly: Boolean = false,
    parental: Boolean = false,
    metadata: Boolean = false,
    recursive: Boolean = false) = {
    if (applicable) {
      val property = new Property(name, typ, source, default, readOnly, parental, metadata, recursive)
      properties += name -> property
    } else {
      // TODO: Flesh out the Resource hierarchy to avoid this ugliness
      properties -= name
    }
  }

  def hasProperty(name: String) = properties.contains(name)
  def hasProperty(property: Property): Boolean = hasProperty(property.name)

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
  
  //// equality
  
  override def equals(x: Any) = try {
    x.getClass == getClass && x.asInstanceOf[Resource].data.equals(data)
  } catch {
    case x: Exception => false
  }

  //// loading from a cRud response

  def fromInfo(info: Chalice): Unit = {
    fromInfo(info, Map("metadata" -> true, "entity" -> false))
    cache.touch(this)
  }

  private def fromInfo(info: Chalice, keys: Map[String,Boolean]) = {
    for ((key,metadata) <- keys) {
      ingest(info(key), metadata)
    }
  }

  private def ingest(raw: Chalice, metadata: Boolean) {
    for ((key, value) <- raw.map) {
      val propertyName: String =
        if (hasProperty(key))
          // eg service.version
          key
        else
          propertyForSource(key) match {
            case Some(property) => {
              // eg service.name from key label
              property.name
            }
            case None => {
              if (is_parent_guid_key(key)) {
                // eg service.servicePlanGuid from key service_plan_guid
                parent_guid_key_to_parentGuidPropertyName(key)
              } else {
                null
              }
            }
          }
      if (propertyName == null) {
        logger.finest(s"Ignoring key '${key}' for resource ${getBriefClassName}")
      } else {
        val property = properties(propertyName)
        if (property.metadata == metadata) {
          logger.finest(s"Setting property '${propertyName}' to '${value}' from '${key}' for resource ${getBriefClassName}")
          setData(propertyName, value, sudo = true)
        } else {
          logger.finest(s"Ignoring property '${propertyName}' in wrong response section for resource ${getBriefClassName}")
        }
      }
    }
  }

  //// property values

  private def hasData(property: Property): Boolean = hasData(property.name)
  private def hasData(propertyName: String) = data.contains(propertyName)

  def setData(property: Property, value: Chalice): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Chalice, sudo: Boolean): Unit = {
    if (!sudo && property.readOnly) throw new ReadOnly(property, value, this)
    data.put(property.name, value)
    dirty += property
  }

  protected def setData(property: Property, value: Any): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Any, sudo: Boolean): Unit = {
    setData(property, Chalice(value), sudo)
  }

  protected def setData(propertyName: String, value: Chalice): Unit = setData(propertyName, value, false)
  protected def setData(propertyName: String, value: Chalice, sudo: Boolean): Unit = {
    if (hasProperty(propertyName)) {
      setData(properties(propertyName), value, sudo)
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  protected def setData(propertyName: String, value: Any): Unit = setData(propertyName, value, false)
  def setData(propertyName: String, value: Any, sudo: Boolean): Unit = {
    setData(propertyName, Chalice(value), sudo)
  }

  private def clearData(propertyName: String) = data.remove(propertyName)

  def getData[T](propertyName: String): T = {
    if (hasProperty(propertyName)) {
      getData[T](properties(propertyName))
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  // Default values can be specified lazily with a function to
  // retrieve the value, rather than the value itself; see for
  // example note [!!&&**&&!!] in AbstractClient
  type LazyDefault = () => Any

  def getData[T](property: Property): T = {
    if (hasProperty(property)) {
      if (hasData(property)) {
        data(property.name).as(property.typ).asInstanceOf[T]
      } else {
        property.default match {
          case Some(func: LazyDefault) => func().asInstanceOf[T]
          case Some(value: Any) => value.asInstanceOf[T]
          case None => throw new MissingRequiredProperty(this, property)
        }
      }
    } else {
      throw new InvalidProperty(property.name, this)
    }
  }
  
  //// model magic via dynamic invocation

  def applyDynamic(noun: String)(args: Any*) = doApplyDynamic(noun, args)

  def doApplyDynamic(noun: String, args: scala.Seq[Any]): Chalice = {
    if (args.isEmpty) {
      // client.services()
      // service.servicePlans()
      // service.servicePlan()
      // etc
      selectDynamic(noun)
    } else if (args.length == 1) {
      args(0) match {
        case s: String => {
          // client.services("abcd1234") means "the service with id=abcd1234"
          applyDynamicNamed(noun)(new Constraint("id", s))
        }
        case i: Int => {
          // client.services(3) means "the 4th service" (yeah, kinda bizarre)
          Chalice(selectDynamic(noun).resources(i))
        }
        case c: Chalice => {
          // previous cases, but where index/id is embedded in a Chalice
          if (c.isTrueInt) {
            applyDynamic(noun)(c.int)
          } else if (c.isString) {
            applyDynamic(noun)(c.string)
          } else {
        	  throw new UnexpectedArguments(noun, args)
          }
        }
      }
    } else {
      throw new UnexpectedArguments(noun, args)
    }
  }
  
  def applyDynamicNamed(noun: String)(constraints: Pair[String,Any]*) = {
    evaluate(noun, new Constraints(constraints))
  }

  def selectDynamic(noun: String) = {
    evaluate(noun, Constraints.NONE)
  }

  def evaluate(noun: String, constraints: Constraints = Constraints.NONE) = {
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
                // note that if the cached resource is dirty, then we won't notice updates.
                // that is not great; but is is better than losing the local changes
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
        constraintWarning(noun, constraints)
        Chalice(getData(noun))
      } else {
        val property = properties(noun)
        // servicePlan.description
        logger.fine(s"Selecting property '${noun}' of resource ${this}")
        constraintWarning(noun, constraints)
        Chalice(getData(noun))
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
        constraintWarning(noun, constraints)
        Chalice(child)
      } else {
        // servicePlan.serviceInstances
        logger.fine(s"Enumerating '${noun}' children of resource ${this}")
        val path = getData[String](childrenPathPropertyName(noun))
        val resources = enumerate(factory, path, constraints)
        if (constraints.unique || constraints.first) {
          // servicePlan.serviceInstances(id = foo) or servicePlan.serviceInstances(first = true)
          Chalice(resources(0))
        } else {
          // servicePlan.serviceInstances
          Chalice(resources)
        }
      }
    } else {
      throw new InvalidProperty(noun, this)
    }
  }
  
  def constraintWarning(noun: String, constraints: Constraints) = {
    if (constraints.nonEmpty) {
      logger.warning(s"Ignoring constraints ${constraints.encode} for ${noun}")
    }
  }

  def updateDynamic(noun: String)(value: Any): Unit = {
    if (hasParent(noun)) {
      var parent: Resource = null
      if (value.isInstanceOf[Resource]) {
        parent = value.asInstanceOf[Resource]
      } else if (value.isInstanceOf[Chalice]) {
        parent = value.asInstanceOf[Chalice].resource
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

  def factoryFor(noun: String) = {
    new Factory(noun, context)
  }

  private def enumerate(factory: Factory, _path: String, constraints: Constraints): Seq[Resource] = {
    var path = _path + constraints.encode
    val resources = new ArrayBuilder.ofRef[Resource]
    do {
      val payload = perform(() => crud.cRud(path)(options))
      // TODO: This check for "resources" smells bad.
      if (payload.map.contains("resources")) {
        resources ++= payload("resources").seq.map(pload => factory.create(pload))
        path = payload("next_url").string
      } else {
        resources += factory.create(payload)
        path = null
      }
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

  def destroy: Any = destroy(recursive = false)

  def destroy(recursive: Boolean = false) = {
    if (hasId) {
      delete(recursive)
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

  // this need to be public for Appendages, so the "_" is to avoid collision
  // with "getUrl" from java_friendly/Generate 
  def _getUrl = {
    if (hasId && !hasData(url)) {
      if (data.size == 1) {
        // if we just have the id, then we'll assume we're lazily
        // fetching a parent -- ie, this is not a problem
        logger.info(s"Creating URL for ${this}")
      } else {
        logger.warning(s"Generating missing URL for ${this}")
      }
      s"${absolutePath(inflector.pluralize(inflector.camelToUnderline(getBriefClassName)))}/${_getId}"
    } else {
      // this will throw an exception if we don't have
      // a URL -- which is the right thing to do 
      getData[String](url)
    }
  }

  // this need to be public for Cache, so the "_" is to avoid collision
  // with "getId" from java_friendly/Generate 
  def _getId = getData[String](id)

  private def hasId = hasData(id)
  private def clearId = clearData(id)

  private def attachParent(parentPropertyName: String, parent: Resource, child: Resource) = {
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
  
  import CRUD._

  protected def create = {
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
    val payload = Chalice(JSON.serialize(Chalice(content)))
    performAndReload(() => crud.Crud(absolutePath)(options)(Some(payload)))
  }

  private def read = {
    performAndReload(() => crud.cRud(_getUrl)(options))
  }

  protected def update = {
    val payload =
      dirty
      .filter(property => property.entity && !property.parental && !property.readOnly)
      .map(property => property.getTrueSource -> getData(property))
      .toMap
    performAndReload(() => crud.crUd(_getUrl)(options)(Some(Chalice(JSON.serialize(Chalice(payload))))))
  }

  private def delete(recursive: Boolean) = {
    val path = _getUrl + (if (recursive) "?recursive=true" else "")
    perform(() => crud.cruD(path)(options))
    cache.eject(this)
  }

  def options = Some(
    Pairs(
      AUTH -> token.authHeader
    )
  )

  protected def performAndReload(performer: () => Response) = {
    // TODO: I think this is wrong.  fromInfo expects a single 'resource'
    // structure from the response payload.  But this gives it the entire
    // payload.  It should be something like:
    // fromInfo(perform(performer)("resources").seq.first)
    // OTOH, it is working fine now for Crud and cRud??!!
    fromInfo(perform(performer))
    clean
  }

  def perform(performer: () => Response) = {
    var response = performer()
    if (!response.ok) {
      if (response.code.get == HttpStatus.SC_UNAUTHORIZED) {
    	val msg = "Request denied (${response.code})"
    	if (authenticator != null) {
    	  if (authenticator()) {
            logger.fine(s"${msg}, but retrying after successful reauthentication")
            response = performer()
          } else {
            logger.fine(s"${msg} and reauthentication failed")
          }
    	} else {
          logger.fine(s"${msg} and reauthentication is impossible")
    	}
      }
    }
    if (response.ok) {
      if (response.hasPayload) {
        response.payload
      } else {
        null
      }
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
      Chalice(
        properties
          .values
          .filter(property => (property.hasDefault || hasData(property)) && !isVerbose(property))
          .map(property => property.name -> safeGetData(property))
          .toMap)
      .toString
    s += '>'
    s.result
  }

  def safeGetData(property: Property) = {
    try {
      if (property.recursive) {
        "<recursive>"
      } else {
        getData(property).toString
      }
    } catch {
      case x: Exception => s"{${x}}"
    }
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
