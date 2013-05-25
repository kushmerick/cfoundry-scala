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
  private val NO_ID = None

  private val url = "url"

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

  property(id, source = "guid", default = Some(NO_ID))
  property("name")
  property("description")
  property(url, readOnly = true)

  //// properties (TODO: should be 'static', but can't just move them to
  //// a companion object due to subclassing.)

  protected def property(name: String, typ: String = "string", source: String = null, filter: Filter = null, default: Option[Any] = None, applicable: Boolean = true, readOnly: Boolean = false) = {
    if (applicable)
      properties += name -> new Property(name, typ, source, filter, default, readOnly)
    else
      // TODO: Flesh out the Resource hierarchy to avoid this ugliness
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
    property(childrenPathPropertyName(childrenName), source = childrenPathSource(children_name), default = absolutePath, readOnly = true)
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

  protected def hasChildren(childrenName: String) = {
    children.contains(childrenName)
  }

  //// parents; ditto.  (TODO: Simplify resouorce definitions by automatically
  //// generate hasA from hasMany [or the other way around].)

  protected def hasA(parentName: String) = {
    parents += parentName
    property(parentName, typ = "resource")
    property(parentGuidPropertyName(parentName))
  }

  protected def hasParent(parentName: String) = {
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

  def fromPayload(payload: Payload): Resource = {
    fromPayload(payload, Seq("metadata", "entity"))
  }

  private def fromPayload(payload: Payload, keys: Seq[String]) = {
    for (key <- keys) {
      ingest(payload(key))
    }
    isDirty = false
    this
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
        setData(propertyName, value, ingesting = true)
      }
    }
  }

  //// property values

  private def hasData(property: Property): Boolean = hasData(property.name)
  private def hasData(propertyName: String) = data.contains(propertyName)

  protected def setData(property: Property, value: Payload): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Payload, ingesting: Boolean): Unit = {
    if (!ingesting && property.readOnly) throw new ReadOnly(property, value, this)
    data.put(property.name, value)
  }

  protected def setData(property: Property, value: Any): Unit = setData(property, value, false)
  protected def setData(property: Property, value: Any, ingesting: Boolean): Unit = {
    setData(property, Payload(value), ingesting)
  }

  protected def setData(propertyName: String, value: Payload): Unit = setData(propertyName, value, false)
  protected def setData(propertyName: String, value: Payload, ingesting: Boolean): Unit = {
    if (hasProperty(propertyName)) {
      setData(properties(propertyName), value, ingesting)
    } else {
      throw new InvalidProperty(propertyName, this)
    }
  }

  protected def setData(propertyName: String, value: Any): Unit = setData(propertyName, value, false)
  protected def setData(propertyName: String, value: Any, ingesting: Boolean): Unit = {
    setData(propertyName, Payload(value), ingesting)
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
          val child = factory.create
          if (!isInstanceOf[ClientResource]) {
            // set servicePlan.service and servicePlan.serviceGuid
            val parentName = inflector.lowerize(getBriefClassName)
            child.setData(parentName, this)
            child.setData(child.parentGuidPropertyName(parentName), getId)
          }
          MagicResource(child)
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
    isDirty = true
  }

  //// sugar for Java (TODO: Write a script that scans the Resources
  //// and automatically generates better Java interop code!)

  def o(noun: String): Magic = {
    selectDynamic(noun)
  }

  // TODO

  //// child creation & enumeration

  private def factoryFor(noun: String) = {
    new Factory(noun, client)
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
      Crud
    } else {
      if (isDirty) crUd
    }
  }

  def isLocalOnly = !hasId

  //// destroy

  def destroy = if (hasId) cruD

  //// refresh

  def refresh = if (hasId) cRud else throw new Unrefreshable(this)

  //// property helpers

  private def getUrl = getData[String](url)

  private def getId = getData[String](id)
  private def hasId = hasData(id)
  private def clearId = clearData(id)

  //// absolute paths such as /v2/service_instances

  private def absolutePath(): String = {
    absolutePath(inflector.pluralize(inflector.camelToUnderline(getBriefClassName)))
  }

  private def absolutePath(plural_class_name: String = null) = {
    s"/v2/${plural_class_name}"
  }

  ///// CRUD operations

  private def Crud = {
    val payload = Map[String, Any]()
    for ((propertyName, property) <- properties) {
      if (propertyName != id && !isChildrenPathPropertyName(propertyName)) {
        payload.put(property.getTrueSource, getData(property))
      }
    }
    for (parentName <- parents) {
      val parent = if (hasData(parentName)) getData[Resource](parentName) else null
      val pgpn = parentGuidPropertyName(parentName)
      var parentGuid = if (hasData(pgpn)) getData[String](pgpn) else null
      if (parent == null && parentGuid == null) {
        throw new NotSaveable(this, parentName)
      } else {
        if (parent != null && parentGuid != null && parent.getId != parentGuid) {
          throw new NotSaveable(this, s"Inconsistent GUID for parent '${parentName}': '${parentGuid}' and '${parent.getId}'")
        }
        if (parentGuid == null) parentGuid = parent.getId
        payload.put(parent_guid_key(parentName), parentGuid)
      }
    }
    performAndReload(() => crud.Crud(absolutePath)(options)(Some(Payload(payload))))
  }

  private def cRud = {
    performAndReload(() => crud.cRud(getUrl)(options))
  }

  private def crUd = {
    val metadata = Map(id -> getId)
    val entity = null // TODO
    val payload = Map("metadata" -> metadata, "entity" -> entity)
    performAndReload(() => crud.crUd(getUrl)(options)(Some(Payload(payload))))
  }

  private def cruD = {
    perform(() => crud.cruD(getUrl)(options))
    clearId
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
          .filter(property => property.hasDefault || hasData(property))
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

}

/*


REQUEST: POST https://api.go.cloudfoundry.com/v2/service_instances
REQUEST_HEADERS:
  Accept : application/json
  Authorization : bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIzZDFiNDVmMC01NGVkLTRmMDgtOTQ4Ni1iNDk1YjIzOWI1NDMiLCJzdWIiOiJjMWU1ODg4NC1jODIwLTQ5NDEtODNkYi05NDk4NDhkMzZkMzYiLCJzY29wZSI6WyJwYXNzd29yZC53cml0ZSIsIm9wZW5pZCIsImNsb3VkX2NvbnRyb2xsZXIud3JpdGUiLCJjbG91ZF9jb250cm9sbGVyLnJlYWQiXSwiY2xpZW50X2lkIjoiY2YiLCJjaWQiOiJjZiIsImdyYW50X3R5cGUiOiJwYXNzd29yZCIsInVzZXJfaWQiOiJjMWU1ODg4NC1jODIwLTQ5NDEtODNkYi05NDk4NDhkMzZkMzYiLCJ1c2VyX25hbWUiOiJuaWNob2xhc2tAdm13YXJlLmNvbSIsImVtYWlsIjoibmljaG9sYXNrQHZtd2FyZS5jb20iLCJpYXQiOjEzNjk0NDg5NTgsImV4cCI6MTM2OTQ5MjE1OCwiaXNzIjoiaHR0cHM6Ly91YWEuZ28uY2xvdWRmb3VuZHJ5LmNvbS9vYXV0aC90b2tlbiIsImF1ZCI6WyJzY2ltIiwib3BlbmlkIiwiY2xvdWRfY29udHJvbGxlciIsInBhc3N3b3JkIl19.x4tWO-NYdJ23Dary5XAl8sjnYxTcZPlMAkSbisJcNhmWkKgi0EwJCU_Zu4YEzoL8HwB_Ra9j47X_P0YXldoxSCxXZDoJXjkA1UVGL4ohtluVlmF-WdIwys6Vg-JU1AHiHNt9ZCNfQBHbZyKFPsN_p2Jj_ILMUrQqd3c2Lio0_NuG47X9Y0tfRkHN2AsH-mtTH_Lp5ovxZ7CNWUZ2uR4Jk7SAua3h8KBGiIS1vzvGN66N0SqN5i0NnmMk2Ng-MBS_qkV1d7h24tS4eX9e--YW1tPqEFulptJ5NXoEmPi8eln2qg4LWtWcTtvJ9OEHjnBYD2NHNWrGNbm3RueQNlfMKA
  Content-Length : 137
  Content-Type : application/json
REQUEST_BODY: {"space_guid":"127665e2-a9f5-452d-8b37-f1df7096c1cb","name":"rds-mysql-4a256","service_plan_guid":"88f404ab-1888-46e1-8d8a-d5d678e971da"}                                                                                                    .  RESPONSE: [201]
RESPONSE_HEADERS:
  connection : keep-alive
  content-length : 1151
  content-type : application/json;charset=utf-8
  date : Sat, 25 May 2013 02:30:57 GMT
  location : /v2/service_instances/861ccfab-8131-4875-b5cf-9a06fe3c5a52
  server : nginx
  x-frame-options : sameorigin
  x-vcap-request-id : 68cf9b1c-ba72-45e6-8d52-e0c4bf3cb3ac
  x-xss-protection : 1; mode=block
RESPONSE_BODY:
{
  "metadata": {
    "guid": "861ccfab-8131-4875-b5cf-9a06fe3c5a52",
    "url": "/v2/service_instances/861ccfab-8131-4875-b5cf-9a06fe3c5a52",
    "created_at": "2013-05-25 02:30:57 +0000",
    "updated_at": null
  },
  "entity": {
    "name": "rds-mysql-4a256",
    "credentials": {
      "name": "d52084060ab0d497b8a873c208baf685d",
      "hostname": "mysql-service-public.clqg2e2w3ecf.us-east-1.rds.amazonaws.com",
      "host": "mysql-service-public.clqg2e2w3ecf.us-east-1.rds.amazonaws.com",
      "port": 3306,
      "user": "usH042nsPn42m",
      "username": "usH042nsPn42m",
      "password": "pwXhEHE7MOsrL",
      "node_id": "rds_mysql_node_10mb_0"
    },
    "service_plan_guid": "88f404ab-1888-46e1-8d8a-d5d678e971da",
    "space_guid": "127665e2-a9f5-452d-8b37-f1df7096c1cb",
    "gateway_data": {
      "plan": "10mb",
      "version": "n/a"
    },
    "dashboard_url": null,
    "service_bindings_url": "/v2/service_instances/861ccfab-8131-4875-b5cf-9a06fe3c5a52/service_bindings",
    "space_url": "/v2/spaces/127665e2-a9f5-452d-8b37-f1df7096c1cb",
    "service_plan_url": "/v2/service_plans/88f404ab-1888-46e1-8d8a-d5d678e971da"
  }
}
<<<     

*/ 