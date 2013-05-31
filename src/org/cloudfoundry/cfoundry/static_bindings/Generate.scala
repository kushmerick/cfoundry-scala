package org.cloudfoundry.cfoundry.static_bindings

import java.io.File
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._

object Generate extends scala.App with ClassNameUtilities {

  val resourceSourceFile = new File(args(0))
  val destDir = new File(args(1))
  
  var resourceClassName = resourceSourceFile.getName
  resourceClassName = resourceClassName.substring(0, resourceClassName.length - ".scala".length)
  
  Console.println("Processing " + resourceSourceFile.getName + " -> " + resourceClassName)
  
  if (resourceClassName == "package" || resourceClassName == "Resource") {
    System.exit(0)
  }
  
  val methods = scala.collection.mutable.ListBuffer[String]()

  class X(_inflector: Inflector) extends ClientContext {
    setInflector(_inflector)
  }
  val inflector = new Inflector
  val context = new X(inflector)

  var resource: Resource = null
  try {
	val factory = new Factory(resourceClassName, context)
    resource = factory.create
    if (resource.isInstanceOf[Resource]) {
      val rresource = resource.asInstanceOf[Resource]
      val properties = rresource.getProperties
      for (propertyName <- properties.keys.toArray.sorted) {
        methods ++= makeMethods(rresource, properties(propertyName))
      }
      for (childrenName <- rresource.getChildren) {
        val childClassName = getSiblingClass(inflector.singularize(inflector.capitalize(childrenName)), resource.getClass).getName
        methods += makeGetter(childrenName, "scala.collection.Seq[" + childClassName + "]")
      }
    } else {
      Console.println(" * Ignored")
    }
  } catch {
    case x: Exception => Console.println(s"Skipping ${resourceClassName}: ${x}")
  }
  
  for (method <- methods.result) {
    Console.println(">> " + method)
  }
  
  def makeMethods(resource: Resource, property: Property): Seq[String] = {
    // getter
    val returnType =
      if (resource.hasParent(property.name) || resource.hasChildren(property.name)) {
        getSiblingClass(inflector.capitalize(property.name), resource.getClass).getName
      } else if (resource.hasChildren(inflector.singularize(property.name))) {
        classOf[Seq[Resource]].getName + s"[${inflector.capitalize(property.name)}]" 
      } else {
        getPayloadType(property.typ).getName
      }
    val methods = new scala.collection.mutable.ArrayBuilder.ofRef[String]
    methods += makeGetter(property.name, returnType)
    // setter
    if (!property.readOnly) {
      val valueType =
        if (resource.hasParent(property.name)) {
          getSiblingClass(inflector.capitalize(property.name), resource.getClass).getName
        } else {
          getPayloadType(property.typ).getName
        }
      methods += makeSetter(property.name, valueType)
    }
    methods.result
  }
  
  def makeGetter(name: String, returnType: String) = {
     "public " + returnType + " get" + inflector.capitalize(name) + "() { (" + returnType + ") selectDynamic(\"" + name + "\"); }"
  }

  def makeSetter(name: String, valueType: String) = {
     "public void set" + inflector.capitalize(name) + "(value: " + valueType + ") { updateDynamic(\"" + name + "\")(value); }"
  }

  def getPayloadType(typ: String) = {
    classOf[Payload].getMethod(typ).getReturnType
  }
  
}