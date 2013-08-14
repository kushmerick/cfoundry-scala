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
    client.applyDynamicNamed(nouns)(new Constraint("id", id)).resource.id.string should equal(id)
    // 2. client.apps(guid = id)
    client.applyDynamicNamed(nouns)(new Constraint("guid", id)).resource.id.string should equal(id)
    // 3. client.apps(id)
    client.applyDynamic(nouns)(id).resource.id.string should equal(id)
  }
  
  def testEnumerationDepth(client: MockedClient, noun: String) {
    // TODO: Note that we enumerate what resources happen to exist on the CF server, which
    // might well be none at all.  In that case, this is still a perfectly valid test -- all
    // all enumerations should return zero resources.  But it would be more interesting for
    // the caller to create some resources prior to calling.
    val resources1 = enumerate(client, noun)
    // two flavors
    // 1. client.apps(depth = 3)
    val resources2 = enumerate(client, noun, "depth" -> 3)
    resources1 should equal(resources2)
    // 2. client.apps(inline_relations_depth = 3)
    val resources3 = enumerate(client, noun, "inline_relations_depth" -> 3)
    resources1 should equal(resources3)
  }
  
}