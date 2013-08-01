package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client.mock._
import org.scalatest.matchers._
import org.scalatest.fixture._

trait CRUDTests extends ShouldMatchers {
  
  def testCRUD(client: MockedClient, noun: String, initialization: Map[String,Any]) = {
    // C
    val resource = sprout(client, noun)
    resource.name = "foobar"
    for((property, value) <- initialization) {
      set(resource, property, value)
    }
    resource.save
    // R
    exists(client, noun, resource) should be(true)
    // U
    resource.name = "foobaz"
    resource.save
    resource.name.string should be("foobaz")
    // D
    resource.destroy
    exists(client, noun, resource) should be(false)
  }
  
  private def sprout(client: MockedClient, noun: String) = {
    client.selectDynamic(noun).resource
  }
  
  private def set(resource: Resource, property: String, value: Any) = {
    resource.updateDynamic(property)(value)
  }
  
  private def enumerate(client: MockedClient, noun: String) = {
    client.selectDynamic(client.getInflector.pluralize(noun)).resources
  }
  
  private def exists(client: MockedClient, noun: String, resource: Resource) = {
    enumerate(client,noun).contains(resource)
  }
  
}