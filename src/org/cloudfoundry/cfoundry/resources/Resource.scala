package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import scala.collection.mutable._

import scala.language.dynamics

class Resource extends Dynamic with ClassNameUtilities {

  var magician: Magician = null

  //// properties (TODO: These should be 'static', but
  //// we can't just move them to the companion object.)

  private val properties = Map[String, Property]()

  protected def property(name: String, typ: String = "string", source: String = null, filter: Filter = null, default: Any = null) = {
    properties += name -> new Property(name, typ, source, filter, default)
  }

  def hasProperty(name: String) = properties.contains(name)
  def hasProperty(property: Property): Boolean = hasProperty(property.name)

  private def propertyForSource(source: String) = {
    properties.values.find(property => source == property.source)
  }

  //// children (ditto)

  private val children = Map[String, Class[_]]()

  protected def one_to_many(childClassName: String) = {
    val c = Class.forName(s"${getClassQualification}.${childClassName}")
    children += childClassName -> c
    property(childUrlPropertyName(childClassName), source = childUrlSource(childClassName))
  }

  def childUrlPropertyName(childClassName: String) = {
    childClassName + "URL"
  }

  private def childUrlSource(childClassName: String) = {
    Inflector.camelToUnderline(childClassName) + "_url"
  }

  def hasChild(resourceClassName: String) = {
    children.contains(resourceClassName)
  }

  // every resource has the following properties (though subclasses
  // might declare the property again, for example to override 'source'  
  property("id", source = "guid")
  property("name")
  property("description")

  //// loading from cRud

  import Resource._

  def fromPayload(payload: Payload): Resource = {
    fromPayload(payload, METADATA, ENTITY)
  }

  private def fromPayload(payload: Payload, keys: String*) = {
    for (key <- keys) {
      ingest(payload(key))
    }
    val x = toString
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

  //// instance property values

  private var data: Map[String, Payload] = Map[String, Payload]()

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

  //// dynamic invocation

  def selectDynamic(noun: String) = {
    magician.selectForResource(this, noun)
  }

  def applyDynamic(method: String)(args: Any*) = {
    //  magician.applyForResource(this, method, args)
  }

  def updateDynamic(method: String, value: Any) = {
    // magician.updateForResource(this, method, value)
  }

  ////

  override def toString = {
    val s = new StringBuilder(s"<${getBriefClassName} ")
    s ++= Payload(
      properties
        .values
        .filter(property => hasData(property))
        .map(property => (property.name -> getData(property)))
        .toMap)
      .pretty
    s += '>'
    s.result
  }

}

object Resource {

  private val METADATA = "metadata"
  private val ENTITY = "entity"
  private val ID = "id"

}