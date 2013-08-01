package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.scalatest._

class AppSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests {
  
  override val login = true
  
  "App" should "be CRUDable" in { client =>
    testCRUD(client, "app", Map("space" -> client.spaces(0)))
  }
  
  it should "be able to use a query to find itself" in { client =>
    val app = client.app.resource
    app.name = "blah"
    app.space = client.spaces(0)
    app.save
    testEnumeration(client, "app", app)
    app.destroy
  }
  
  it should "be able to upload and download bits" in { client =>
    pending
  }

}