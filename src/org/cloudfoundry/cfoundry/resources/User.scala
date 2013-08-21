package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.exceptions._
import scala.beans._

// TODO: Update to support a CF that federates authz to an
// external SAML IDP using saml_login

class User(client: ClientContext) extends Resource(client) with UserJF {

  property("admin", typ = "bool", default = Some(false))
  property("name", applicable = false)
  property("description", applicable = false)

  // The "User" resource is atypical:
  //  - it exists "partly" in UAADB (username, emails, etc) and "partly"
  //    in CCDB (admin flag), and the records are connected
  //    to one another by a common guid. 
  //  - unlike other resources, "POST /v2/users" requires this guid property as
  //    an input in the request payload, rather than generating it internally.
  //  - we can't use the standard Resource#property mechanism for the UAADB
  //    properties, so instead this class manages them explicitly.
  //  - we need to intercept the four CRUD operations (which handle CCDB) so
  //    that we can also update UAADB.

  private lazy val uaaClient = context.uaaClient()

  var username: String = null
  def getUsername = username
  def setUsername(x: String) = username = x

  var email: String = null
  def getEmail = email
  def setEmail(x: String) = email = x

  var givenName: String = null
  def getGivenName = givenName
  def setGivenName(x: String) = givenName = x

  var familyName: String = null
  def getFamilyName = familyName
  def setFamilyName(x: String) = familyName = x
  
  // Password & old-password are write-only.  Specify a user's password when created
  // like this:
  //   user = client.user
  //   user.setPassword("p@$$w0rd")
  //       ...
  //   user.save
  // Change a user's password by setting the old and new passwords and then saving,
  // like this:
  //   user.setPassword("$3cr3+")
  //   user.setOldPassword("p@$$w0rd"
  //   user.save
  var password: String = null
  def getPassword = password
  def setPassword(p: String) = password = p
  var oldPassword: String = null
  def getOldPassword = oldPassword
  def setOldPassword(p: String) = oldPassword = p
  def wipePasswords = { password = null; oldPassword = null }
  
  // create -- order is important: create in UAADB first to generate the guid, then create in CCDB
  override def create = { uaaCreate; super.create }
  def uaaCreate = uaaClient.create(this)
  override def createPayload = { val p = super.createPayload; p("guid") = _getId; p }

  // read -- order is important: read from CCDB first so we know which user to read from UAADB [TODO: Huh?!]
  override def read = { super.read; uaaRead }
  def uaaRead = uaaClient.read(this)

  // update -- order is not important because only the guid is shared and it never changes
  override def update = { uaaUpdate; super.update }
  def uaaUpdate = uaaClient.update(this)

  // destroy -- order is important: it would be bad to leave a CCDB guid with no matching record in UAADB
  override def delete(recursive: Boolean) = { super.delete(recursive); uaaDelete }
  def uaaDelete = uaaClient.delete(this)

}
