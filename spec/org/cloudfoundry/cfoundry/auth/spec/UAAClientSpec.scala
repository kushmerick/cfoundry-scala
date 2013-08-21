package org.cloudfoundry.cfoundry.client.spec

import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.resources.mock._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.config._
import org.scalatest.fixture._
import org.scalatest.matchers._

class UAAClientSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with ResourceFixture {

  override val login = true

  val email = "foo@bar.com"
  val password = "p@$$w0rd"

  def vanillaUser(client: MockedClient, saved: Boolean = false, test: User => Any) = {
    give a "user" saved saved where { resource =>
      val user = resource.asInstanceOf[User]
      user.setEmail(email)
      user.setPassword(password)
    } from client to { resource =>
      val token = client.token
      try {
        test(resource.asInstanceOf[User])
      } finally {
        client.token = token // in case test logs in as someone else
      }
    }
  }

  "UAAClient" should "create users with default values" in { client =>
    vanillaUser(client, false, { user =>
      user.uaaCreate
      client.login(user.username, password)
      client.authenticated should be(true)
      user.id.isNull should be(false)
      user.username should be(email)
      user.email should equal(email)
      user.givenName should be(email)
      user.familyName should be(email)
    })
  }

  it should "create users with custom values" in { client =>
    vanillaUser(client, false, { user =>
      user.username = "foo"
      user.givenName = "fooGiven"
      user.familyName = "fooFamily"
      user.uaaCreate
      // the following are less trivial than it might appear: UAAClient#fromPayload
      // overwrites these properties from the "POST /Users" response payload
      user.username should be("foo")
      user.givenName should be("fooGiven")
      user.familyName should be("fooFamily")
    })
  }

  it should "read users" in { client =>
    vanillaUser(client, true, { user =>
      val id = user.id.string
      user.uaaRead
      // as discussed above, the following are less trivial than it might appear
      user.id.string should be(id)
      user.username should be(email)
      user.email should be(email)
      user.givenName should be(email)
      user.familyName should be(email)
    })
  }

  it should "update users" in { client =>
    vanillaUser(client, true, { user =>
      user.username = "foo"
      user.email = "bar@foo.com"
      user.givenName = "fooGiven"
      user.familyName = "fooFamily"
      user.uaaUpdate
      // the discussed above, following are less trivial than it might appear
      user.username should be("foo")
      user.email should be("bar@foo.com")
      user.givenName should be("fooGiven")
      user.familyName should be("fooFamily")
    })
  }

  it should "change passwords" in { client =>
    vanillaUser(client, true, { user =>
      try {
        client.login(user.username, password)
        client.authenticated should be(true)
        val newPassword = "f006@r"
        user.setPassword(newPassword)
        user.setOldPassword(password)
        user.uaaUpdate
        client.logout
        client.login(user.username, newPassword)
        client.authenticated should be(true)
      }
    })
  }

}
