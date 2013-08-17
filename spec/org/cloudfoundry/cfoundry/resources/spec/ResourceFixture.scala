package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.client.mock._

trait ResourceFixture {
  
  // A DSL for fixture resources that are auto-generated (in the spirit of
  // machinist blueprints), passed to a test, and destroyed (including ancestors)
  // when the test completes (even if the block raises an exception).  For example:
  //     give a "space" from client to { space => ... }

  object give {
    def a(noun: String) = new DSL(noun, null)
    def an(noun: String) = a(noun)
  }

  protected class DSL(noun: String, client: MockedClient) {
    def from(client: MockedClient) = new DSL(noun, client)
    def to(test: Resource => Any) = manageResource(noun, client, test)
  }

  private def manageResource(noun: String, client: MockedClient, test: Resource => Any) = {
    var roots: Seq[Resource] = null
    var torndown = false
    var teardown = (roots: Seq[Resource]) => { destroyRoots(roots); torndown = true }
    var setupException: Exception = null
    var teardownException: Exception = null
    var blockException: Exception = null
    try {
      createResource(client, noun) match {
        case (resource, _roots) => {
          roots = _roots
          try {
            test(resource)
          } catch {
            case x: Exception => blockException = x
          }
        }
      }
      try {
    	teardown(roots)
      } catch { 
         case x: Exception => teardownException = x
       }
    } catch {
      case x: Exception => setupException = x
    }
    if (!torndown && teardownException == null) {
      try {
        teardown(roots)
      } catch {
        case x: Exception => teardownException = x
      }
    }
    val exceptions = List[Exception](setupException, blockException, teardownException).filterNot(_ == null)
    if (exceptions.nonEmpty) {
      val cause = if (exceptions.size == 1) exceptions(0) else new MultipleCauses(exceptions)
      throw new CFoundryException(s"Exception with '${noun}' fixture", cause = cause)
    }
  }

  private def destroyRoots(roots: Seq[Resource]) = {
    if (roots != null) {
      roots.foreach(root => root.destroy(recursive = true))
    }
  }

  private def createResource(client: MockedClient, noun: String): (Resource, Seq[Resource]) = {
    // recursively create a foo and any necessary parents; returns the resource, and also
    // the roots of the forest, so that everything can be cleaned up
	val resource = client.factoryFor(noun).create
	for ((propertyName, property) <- resource.properties) {
	  if (!property.parental && !property.readOnly && property.default.isEmpty) {
        resource.setData(property, Chalice(fakeFor(noun, property)))
	  }
	}
	var roots = Seq[Resource]()
	for ((parentName, parentClass) <- resource.parents) {
	  createResource(client, parentName) match {
	    case (parent, parentRoots) => {
	      resource.updateDynamic(parentName)(parent)
	      roots ++= parentRoots
	    }
	  }
	}
	try {
      resource.save
	} catch {
	  case x: Exception => {
	    var saveException = x
	    try {
	      destroyRoots(roots)
	    } catch {
	      case y: Exception => saveException = new MultipleCauses(x, y)
	    }
	    throw saveException
	  }
	}
	if (roots.isEmpty) {
	  // if a resource has parents, then cleaning up the roots will destroy up the resource
	  // itself.  but if a resource doesn't have any parents, we need to ensure we destory
	  // it during cleanup.
	  roots = Seq(resource)
	}
	resource -> roots
  }

  private def fakeFor(noun: String, property: Property) = {
	// TODO -- this is probably way too simplistic...
    lazy val guid = "" // TODO: See explanation in TODO.txt
    lazy val f = s"fake_${noun}_${property.name}_${guid}"
    val fake = property.typ match {
      case "url"    => s"http://${f}"
      case "string" => f
      case "bool"   => true
      case _        => null
    }
    if (fake == null) {
      throw new CFoundryException(s"Can't make a fake for ${noun}'s ${property.name}")
    } else {
      fake
    }
  }

}