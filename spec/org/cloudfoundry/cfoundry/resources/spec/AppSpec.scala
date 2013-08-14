package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client.mock.MockedClientFixture

class AppSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true

  "App" should "be CRUDable" in { client =>
     give a "space" from client to { space =>
      testCRUD(client, "app", Map("space" -> space))
    }
  }

  it should "be able to use a query to find itself" in { client =>
    give an "app" from client to { app =>
      testEnumerationId(client, "app", app)
    }
  }

  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "app")
  }

  it should "be able to upload and download bits" in { client =>
    pending
    /*
    val bits = Array[Byte](1,2,3,4,5)
    val app = client.app.resource
    app.name = "blah"
    app.space = client.spaces(first = true)
    app.bits = bits
    app.save
    client.apps(name = "blah")(first = true).bits should equal(bits)
    */
  }

}