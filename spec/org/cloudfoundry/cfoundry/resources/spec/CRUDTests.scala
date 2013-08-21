package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.exceptions._
import org.scalatest.matchers._
import org.scalatest.fixture._
import scala.util._

trait CRUDTests extends ShouldMatchers {

  def testCRUD(
    client: MockedClient,
    noun: String,
    initialization: Map[String, Any] = Map(),
    beforeSave: Resource => Any = null,
    updateTest0: Triple[String,Any,Any] = null
  ) = {
    var updateTest = updateTest0
    if (updateTest == null) {
      updateTest = ("name", s"testcrud_${noun}", s"testcrud_${noun}_updated")
    }
    // C
    var resource = client.factoryFor(noun).create
    resource.updateDynamic(updateTest._1)(updateTest._2)
    for ((property, value) <- initialization) {
      resource.updateDynamic(property)(value)
    }
    if (beforeSave != null) beforeSave(resource)
    resource.save
    // R
    exists(client, noun, resource) should be(true) // TODO: if this throws an exception, then we will never destroy resource
    // U
    resource.updateDynamic(updateTest._1)(updateTest._3)
    resource.save                                  // TODO: ditto
    resource.selectDynamic(updateTest._1).string should be(updateTest._3.toString)
    // D
    resource.destroy                               // TODO: related to the earlier notes: put this in a "finally"?
    exists(client, noun, resource) should be(false)
  }
  
  protected def enumerate(client: MockedClient, noun: String, constraints: Pair[String,Any]*) = {
    client.evaluate(client.getInflector.pluralize(noun), new Constraints(constraints)).resources
  }
  
  private def exists(client: MockedClient, noun: String, resource: Resource) = {
    enumerate(client,noun).contains(resource)
  }
  
}