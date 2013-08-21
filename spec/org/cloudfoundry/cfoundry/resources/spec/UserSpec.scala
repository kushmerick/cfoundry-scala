package org.cloudfoundry.cfoundry.resources.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._
import org.cloudfoundry.cfoundry.resources._
import org.scalatest.fixture._
import org.scalatest.matchers._

class UserSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with ResourceFixture {

  override protected val login = true
  
  "User" should "be CRUDable" in { client =>
    testCRUD(client, "user", Map(), { resource =>
        val user = resource.asInstanceOf[User]
        user.email = "foo@bar.com"
        user.password = "p@$$w0rd"
      },
      ("admin", false, true) // this is what we change for the "U" part of the test
    )
  }
  
  // TODO: EnumerationTests?

}