package org.cloudfoundry.cfoundry.java_friendly

import java.io.{ Console => _, _ }
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._

object Generate extends scala.App with ClassNameUtilities {

  Console.println(args(0))
  Console.println(args(1))

  val resourceSourceFile = new File(args(0))
  val destDir = args(1)

  var resourceClassName = resourceSourceFile.getName
  resourceClassName = resourceClassName.substring(0, resourceClassName.length - ".scala".length)

  Console.println("Processing " + resourceSourceFile.getName + " -> " + resourceClassName)

  if (resourceClassName == "package" || resourceClassName == "Resource") {
    Console.println("Skipping " + resourceSourceFile.getName)
    System.exit(0)
  }

  class Context(_inflector: Inflector) extends ClientContext {
    setInflector(_inflector)
  }
  val inflector = new Inflector
  val context = new Context(inflector)

  try {
    val isClient = resourceClassName == getBriefClassName(classOf[Client])
    var resource = if (isClient) {
      val client = new FakeClient // hack: Factory can't create Clients
      populateClientChildren(client)
      client
    } else {
      (new Factory(resourceClassName, context)).create
    }
    var resourceClass: Class[_] = if (isClient)
      classOf[Client] // hack: Client needs to receive FakeClient's JF
    else
      resource.getClass
    if (resource.isInstanceOf[Resource]) {
      val methods = scala.collection.mutable.ListBuffer[String]()
      // properties
      val properties = resource.getProperties
      for (propertyName <- properties.keys.toArray.sorted) {
        methods ++= makeMethods(resource, properties(propertyName))
      }
      // enumerate & create children
      for (childrenName <- resource.getChildren) {
        val childClassName = getResourceClassName(inflector.singularize(inflector.capitalize(childrenName)))
        methods += makeGetter(childrenName, getListResourceClassName(childClassName), getSeqResourceClassName(childClassName), Magic.RESOURCES)
        methods += makeCreator(inflector.singularize(childrenName), childClassName)
      }
      writeMethods(resourceClass, methods.result)
    } else {
      throw new RuntimeException(s"${resourceClassName} is not a Resource")
    }
  } catch {
    case x: Exception => {
      Console.println(s"Skipping ${resourceClassName}: ${x}")
      // x.printStackTrace()
    }
  }

  def makeMethods(resource: Resource, property: Property): Seq[String] = {
    val methods = new scala.collection.mutable.ArrayBuilder.ofRef[String]
    // getter
    val (returnType, actualType, deMagicifier) =
      if (resource.hasParent(property.name) || resource.hasChildren(property.name)) {
        val returnType = getPropertyClassName(property)
        (returnType, returnType, Magic.RESOURCE)
      } else if (resource.hasChildren(inflector.singularize(property.name))) {
        (getListPropertyClassName(property), getSeqPropertyClassName(property), Magic.RESOURCES)
      } else {
        val returnType = getChaliceTypeName(property.typ)
        (returnType, returnType, Magic.PROP)
      }
    methods += makeGetter(property.name, returnType, actualType, deMagicifier)
    // setter
    if (!property.readOnly) {
      val valueType =
        if (resource.hasParent(property.name)) {
          getSiblingClass(inflector.capitalize(property.name), resource.getClass).getName
        } else {
          getChaliceTypeName(property.typ)
        }
      methods += makeSetter(property.name, valueType)
    }
    methods.result
  }

  def getResourceClassName(className: String) = {
    getSiblingClass(className, classOf[Resource]).getName
  }

  def getPropertyClassName(property: Property) = {
    getResourceClassName(inflector.capitalize(property.name))
  }

  def getSeqPropertyClassName(property: Property) = {
    getSeqResourceClassName(getPropertyClassName(property))
  }

  def getListPropertyClassName(property: Property) = {
    getListResourceClassName(getPropertyClassName(property))
  }

  def getSeqResourceClassName(className: String) = {
    getCollectionResourceClassName(classOf[scala.collection.Seq[Any]], className)
  }

  def getListResourceClassName(className: String) = {
    getCollectionResourceClassName(classOf[java.util.List[Any]], className)
  }

  def getCollectionResourceClassName(collectionClass: Class[_], className: String) = {
    collectionClass.getName + "[" + className + "]"
  }

  def makeGetter(name: String, returnType: String, actualType: String, deMagicifier: String) = {
    "def get" + inflector.capitalize(inflector.underlineToCamel(name)) + ": " + returnType + " = selectDynamic(\"" + name + "\")." + deMagicifier + ".asInstanceOf[" + actualType + "]"
  }

  def makeSetter(name: String, valueType: String) = {
    "def set" + inflector.capitalize(inflector.underlineToCamel(name)) + "(value: " + valueType + "): Unit = updateDynamic(\"" + name + "\")(value)"
  }

  def makeCreator(name: String, resourceType: String) = {
    "def new" + inflector.capitalize(inflector.underlineToCamel(name)) + ": " + resourceType + " = selectDynamic(\"" + name + "\").resource.asInstanceOf[" + resourceType + "]"
  }

  def getChaliceTypeName(typ: String) = {
    val typeMap = Map[Class[_],Class[_]](
      classOf[Int] -> classOf[java.lang.Integer],
      classOf[Double]-> classOf[java.lang.Double] 
     )
    var c = classOf[Chalice].getMethod(typ).getReturnType
    if (typeMap.contains(c)) c = typeMap(c)
    c.getName
  }
  
  def writeMethods(resourceClass: Class[_], methods: Seq[String]) {
    var packageName = resourceClass.getPackage.getName
    var path = packageName.replace('.', File.separatorChar)
    val className = getBriefClassName(resourceClass)
    path = destDir + File.separator + path + File.separator + "java_friendly" + File.separator + className + "JF.scala"
    val outfile = new File(path)
    var stream: PrintStream = null
    try {
      outfile.getParentFile.mkdirs
      stream = new PrintStream(new FileOutputStream(outfile))
      stream.println(traitPrefix(packageName + ".java_friendly", className))
      methods.foreach(method => stream.println("  " + method))
      stream.println(traitSuffix)
      Console.println("Wrote " + methods.size + " methods to " + outfile)
    } finally {
      if (stream != null) stream.close
    }
  }

  def traitPrefix(packageName: String, className: String) = {
    s"// DO NOT EDIT -- Automagically generated at ${new java.sql.Timestamp(System.currentTimeMillis())} by ${getClass.getName}\n" +
      s"package ${packageName}\n" +
      s"import scala.collection.JavaConversions._\n" +
      s"trait ${className}JF {\n" +
      "  def selectDynamic(noun: String): org.cloudfoundry.cfoundry.resources.Magic\n" +
      "  def updateDynamic(noun: String)(value: Any): Unit"
  }

  def traitSuffix = {
    "}"
  }

  import org.cloudfoundry.cfoundry.http.mock._
  class FakeClient extends AbstractClient[MockCRUD](MockCRUD.factory, "foo", null) {
    override protected lazy val cloudfoundryVersion = 0
  }

  def populateClientChildren(client: FakeClient) {
    // (Very ugly) workaround for the (perfectly reasonable) fact that Client children
    // are registered lazily when "hasChildren" is called.
    var resourcesDir = (new File(destDir)).getParentFile.getParentFile // .../cfoundry-scala/java_friendly/src ==> .../cfoundry-scala
    resourcesDir = new File(resourcesDir, "src") // .../cfoundry-scala/src
    resourcesDir = new File(resourcesDir, classOf[Resource].getPackage.getName.replace('.', '/')) // .../cfoundry-scala/src/org/cloudfoundry/cfoundry/resources
    for (resourceFile <- resourcesDir.listFiles) {
      var className = resourceFile.getName
      className = className.substring(0, className.length - ".scala".length)
      client.hasChildren(inflector.lowerize(className))
    }
  }

}