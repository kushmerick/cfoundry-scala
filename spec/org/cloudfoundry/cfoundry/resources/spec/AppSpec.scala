package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class AppSpec extends FlatSpec with ShouldMatchers with MockedClientFixture {
  
  override val login = true
  
  "App" should "be CRUDable" in { client =>
    // C
    val app = client.app.resource
    app.name = "blah"
    app.space = client.spaces(0)
    app.save
    // R
    client.apps.contains(app) should be(true)
    // U
    app.name = "blug"
    app.save
    app.name.string should be("blug")
    // D
    app.destroy
    client.apps.contains(app) should be(false)
  }
  
  it should "be able to use a query to find itself" in { client =>
    val app = client.app.resource
    app.name = "blah"
    app.space = client.spaces(0)
    app.save
    val id = app.id.string
    // three flavors
    client.apps(id = id).id.string should equal(id)
    client.apps(guid = id).id.string should equal(id)
    client.apps(id).id.string should equal(id)
    app.destroy
  }
  
  it should "be able to upload and download bits" in { client =>
  }

}