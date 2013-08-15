package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._

class LoginClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, logger: Logger) {

  val crud = crudFactory(endpoint, logger)

  def login(username: String, password: String) = loginGeneric(username, "password", password)
  
  private def loginGeneric(username: String, pwKey: String, pwVal: String) = {
    val content = Pairs("username" -> username, pwKey -> pwVal)
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
    val payload = Chalice((Pairs("grant_type" -> grantType) ++ content).formEncode)
    val response = crud.Crud("/oauth/token")(LOGIN_OPTIONS)(Some(payload))
    if (response.ok) {
      Left(new Token(response.payload))
    } else {
      Right(response)
    }      
  }

  private lazy val LOGIN_OPTIONS = Some(
    Pairs(
      CT -> ctFORM,
      AUTH -> "Basic Y2Y6" // TODO: b64("cf:") = Y2Y6, but -- err, umm, ... Huh?!?!
    )
  )

  // Warning: The following alleged SSO support is a ridiculous hack that doesn't work.
    
  def loginSso(username: String) = loginGeneric(username, "passcode", getPasscode)
  
  private lazy val Passcode = """\W(\d{8})\W""".r

  def getPasscode = {
    val response = crud.cRud("/passcode")(LOGIN_OPTIONS)
    if (response.ok) {
      val payload = response.payload
      val body = if (payload.isBlob) new String(payload.blob) else payload.string 
      body match {
        case Passcode(passcode) => passcode
    	case _ => throw new SSOFailure(message = "Missing passcode", response = response)
      }
    } else {
      throw new SSOFailure(response = response)
    }
  }

}