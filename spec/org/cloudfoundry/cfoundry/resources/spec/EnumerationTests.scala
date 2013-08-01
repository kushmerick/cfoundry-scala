package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client.mock._
import org.scalatest.matchers._
import org.scalatest.fixture._

trait EnumerationTests extends ShouldMatchers with CRUDTests {
  
  def testEnumerationId(client: MockedClient, noun: String, resource: Resource) = {
    val id = resource.id.string
    val nouns = client.getInflector.pluralize(noun)
    // three flavors
    // 1. client.apps(id = id)
    client.applyDynamicNamed(nouns)(new Constraint("id", id)).id.string should equal(id)
    // 2. client.apps(guid = id)
    client.applyDynamicNamed(nouns)(new Constraint("guid", id)).id.string should equal(id)
    // 3. client.apps(id)
    client.applyDynamic(nouns)(id).id.string should equal(id)
  }
  
  def testEnumerationDepth(client: MockedClient, noun: String) {
    val resources1 = enumerate(client, noun)
    val resources2 = enumerate(client, noun, ("depth" -> 3))
    resources1 should equal(resources2)
  }
  
}