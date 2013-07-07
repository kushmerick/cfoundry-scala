package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._

class LoginClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, logger: Logger) {

  val crud = crudFactory(endpoint, logger)

  def login(username: String, password: String) = {
    val content = Pairs(
      "grant_type" -> "password",
      "username" -> username,
      "password" -> password)
    val payload = content.formEncode
    val response = crud.Crud("/oauth/token")(LOGIN_OPTIONS)(Some(payload))
    if (response.ok) {
      new Token(response.payload)
    } else {
      throw new NotAuthorized(response, username)
    }
  }

  private lazy val LOGIN_OPTIONS = Some(Pairs(
    "Content-Type" -> HttpCRUD.FORM_ENCODED,
    "Authorization" -> "Basic Y2Y6")) // TODO: What?!!?

}