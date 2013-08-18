package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.client.mock._

trait ResourceFixture {

  // A DSL for fixture resources that are auto-generated (in the spirit of
  // machinist blueprints), passed to a test, and destroyed (including ancestors)
  // when the test completes (even if the block raises an exception).
  //
  // For example:
  //
  //     give a "space" with "name" -> "foo" with "org" -> org from client to { space => ... }

  object give {
    def a(noun: String) = new DSL(noun, Map(), null)
    def an(noun: String) = a(noun)
  }

  type Where = Pair[String, Any]
  type Wheres = Map[String, Any]

  protected class DSL(noun: String, wheres: Wheres, client: MockedClient) {

    def where(w: Where) = new DSL(noun, wheres + w, client)
    def from(client: MockedClient) = new DSL(noun, wheres, client)
    def to(test: Resource => Any) = manageResource(test)

    private def manageResource(test: Resource => Any) = {
      var roots: Seq[Resource] = null
      var torndown = false
      var teardown = (roots: Seq[Resource]) => { destroyRoots(roots); torndown = true }
      var setupException: Exception = null
      var teardownException: Exception = null
      var blockException: Exception = null
      try {
        createResource(client, noun, wheres) match {
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

  }

  private def destroyRoots(roots: Seq[Resource]) = {
    if (roots != null) {
      roots.foreach(root => root.destroy(recursive = true))
    }
  }

  private def createResource(client: MockedClient, noun: String, wheres: Wheres): (Resource, Seq[Resource]) = {
    // recursively create a foo and any necessary parents; returns the resource, and also
    // the roots of the forest, so that everything can be cleaned up
    val resource = client.factoryFor(noun).create
    for ((propertyName, property) <- resource.properties) {
      if (!property.parental && !property.readOnly && property.default.isEmpty) {
        val pn = property.name
        val pv = whereOrElse(wheres, pn, { fakeFor(noun, property) })
        resource.updateDynamic(pn)(pv)
      }
    }
    var roots = Seq[Resource]()
    for ((pn, pc) <- resource.parents) {
      val pv = whereOrElse(wheres, pn, { createResource(client, pn, Map()) match { case (p, rs) => { roots ++= rs; p } } })
      resource.updateDynamic(pn)(pv)
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

  private def whereOrElse(wheres: Wheres, propertyName: String, els: => Any) = {
    wheres.get(propertyName) match {
      case Some(pv) => pv
      case None => els
    }
  }

  private def fakeFor(noun: String, property: Property) = {
    // TODO -- this is probably way too simplistic...
    lazy val guid = "" // TODO: See explanation in TODO.txt
    lazy val f = s"fake_${noun}_${property.name}_${guid}"
    val fake = property.typ match {
      case "url" => s"http://${f}"
      case "string" => f
      case "bool" => true
      case _ => null
    }
    if (fake == null) {
      throw new CFoundryException(s"Can't make a fake for ${noun}'s ${property.name}")
    } else {
      fake
    }
  }

}
