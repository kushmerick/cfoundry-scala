package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._

class LoginClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, logger: Logger) {

  val crud = crudFactory(endpoint, logger)

  def login(username: String, password: String) = {
    val content = Pairs("username" -> username, "password" -> password)
    getToken("password", content) match {
      case Left(token) => token
      case Right(response) => throw new NotAuthorized(response, username)
    }
  }
  
  def refresh(token: Token) = {
    if (token != null) {
      val content = Pairs("refresh_token" -> token.refreshToken)
      getToken("refresh_token", content) match {
        case Left(token) => Some(token)
        case _ => None
      }
    } else {
      None
    }
  }
  
  private def getToken(grantType: String, content: Pairs) = {
    val payload = (Pairs("grant_type" -> grantType) ++ content).formEncode
    val response = crud.Crud("/oauth/token")(LOGIN_OPTIONS)(Some(payload))
    if (response.ok) {
      Left(new Token(response.payload))
    } else {
      Right(response)
    }      
  }

  private lazy val LOGIN_OPTIONS = Some(Pairs(
    "Content-Type" -> HttpCRUD.FORM_ENCODED,
    "Authorization" -> "Basic Y2Y6")) // TODO: b64("cf:") = Y2Y6, but -- err, umm, ... Huh?!?!

}