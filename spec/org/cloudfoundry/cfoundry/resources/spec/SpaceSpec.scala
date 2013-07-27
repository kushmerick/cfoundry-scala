package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class SpacSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {
  
  override val login = true
  
  "Space" should "should be CRUDable" in { client =>
    // C
    val space = client.space.resource
    space.name = "blah"
    space.organization = client.organizations(0)
    space.save
    // R
    client.spaces.contains(space) should be(true)
    // U
    space.name = "blug"
    space.save
    space.name.string should be("blug")
    // D
    space.destroy
    client.spaces.contains(space) should be(false)
  }

}