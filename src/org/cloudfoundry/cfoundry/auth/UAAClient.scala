package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

class UAAClient[+TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, context: ClientContext) {
  
  import UAAClient._

  val crud = crudFactory(endpoint, context.getLogger)
  
  def create(user: User) = {
    val payload = toPayload(user, password = true)
    val response = crud.Crud(USERS)(user.options)(payload -> ctJSON)
    if (response.ok) {
      fromPayload(user, response.payload)
      // so "update" knows that the next "save" is not a password change,
      // and to force the user to actually set the old password 
      user.wipePasswords
    } else {
      throw new BadResponse(response)
    }
  }

  def read(user: User) = {
    val response = crud.cRud(USERS, user.id.string)(user.options)
    if (response.ok) {
      fromPayload(user, response.payload)
    } else {
      throw new BadResponse(response)
    }
  }

  def update(user: User) = {
    // step 1: update properties (TODO: Use If-Match and/or a 'dirty' flag to to avoid an API call
    // if nothing has changed -- ie just a password change?)
    val payload = toPayload(user, password = false)
    val options = Some(user.options.get ++ IFMATCH)
    val response = crud.crUd(USERS, user.id.string)(options)(payload -> ctJSON)
    if (response.ok) {
      fromPayload(user, response.payload)
    } else {
      throw new BadResponse(response)
    }
    // step 2: change password
    if (user.password != null && user.oldPassword != null) {
      val payload = payloadify(
        Map(
          PASSWORD -> user.password,
          OLDPASSWORD -> user.oldPassword
        )
      )
      val response = crud.crUd(USERS, user.id.string, PASSWORD)(user.options)(payload -> ctJSON)
      if (response.ok) {
        // so "update" knows that the next "save" is not a password change,
        // and to force the user to actually set the old password 
        user.wipePasswords
      } else {
        throw new BadResponse(response)
      }
    }
  }

  def delete(user: User) = {
    if (!user.id.isNull) {
      val response = crud.cruD(USERS, user.id.string)(user.options)
      if (!response.ok && !response.notfound) {
        // 404 is weird, but let's not complain
        throw new BadResponse(response)
      }
    }
  }
  
  // Packing/unpacking from UAA's HTTP JSON payloads <==> properties of the User resource. Note
  // that the API payloads have more than one email address, but in fact UAADB supports just one:
  // https://github.com/cloudfoundry/uaa/blob/master/common/src/main/sql/schema.sql.vpp#L22
  
  private def toPayload(user: User, password: Boolean) = {
    // Like the "cfoundry" gem, only "email" is required; it is used as
    // the default value for all the other properties
    val fallback = (x: String) => if (x == null) user.email else x
    val payload = scala.collection.mutable.Map(
      USERNAME -> fallback(user.username),
      EMAILS -> Seq(Map(VALUE -> user.email)),
      NAME -> Map(
        GIVENNAME -> fallback(user.givenName),
        FAMILYNAME -> fallback(user.familyName)
      )
    )
    if (password) payload(PASSWORD) = user.password
    payloadify(payload)
  }
  
  private def payloadify(payload: scala.collection.Map[String,Any]) = Some(Chalice(JSON.serialize(Chalice(payload))))
  
  private def fromPayload(user: User, payload: Chalice) = {
    user.setData("id", payload("id").string, sudo = true)
    user.username = payload(USERNAME).string
    val emails = payload(EMAILS).seq
    if (emails.nonEmpty) user.email = emails.head("value").string
    val name = payload(NAME).map
    user.givenName = name(GIVENNAME).string
    user.familyName = name(FAMILYNAME).string
  }

}

object UAAClient {

  private val USERS = "/Users"

  private val USERNAME = "userName"
  private val NAME = "name"
  private val GIVENNAME = "givenName"
  private val FAMILYNAME = "familyName"
  private val EMAILS = "emails"
  private val VALUE = "value"
  private val PASSWORD = "password"
  private val OLDPASSWORD = "oldPassword"
    
  private val IFMATCH = Pairs("If-Match" -> "*")

}
