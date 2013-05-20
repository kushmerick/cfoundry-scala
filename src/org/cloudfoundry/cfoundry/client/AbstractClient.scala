package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.resources._
import scala.language.dynamics
import java.util.logging._

abstract class AbstractClient[TCRUD <: CRUD](crudFactory: (String,Logger) => TCRUD, target: String, logger: Logger = null) extends Dynamic {

  import AbstractClient._
  
  private val crud = crudFactory(target, logger);
  
  lazy private val uaaClient: UAAClient[TCRUD] =
    new UAAClient[TCRUD](crudFactory, discoverEndpoint(UAA_ENDPOINT), logger)
  lazy private val loginClient: LoginClient[TCRUD] =
    new LoginClient[TCRUD](crudFactory, discoverEndpoint(LOGIN_ENDPOINT), logger)
    
  private var tokenProvider = new TokenProvider(UNAUTHENTICATED)
 
  // convert paths specified by strings or string sequences
  // to the PathCompoment expected by CRUD
  implicit def s2pc(s: String) = Left(s)
  implicit def sseq2pc(sseq: Seq[String]) = Right(sseq)

  private def discoverEndpoint(endpointKey: String) = {
    val response = crud.read("info")()
    if (response.ok) {
      response.payload(endpointKey).string
    } else {
      throw new BadResponse(response)
    }
  }
  
  ////////

  def useToken(token: Token) = {
    tokenProvider.token = token
  }
  
  def login(username: String, password: String) = {
    tokenProvider.token = loginClient.login(username, password)
  }

  def logout = {
    tokenProvider.token = UNAUTHENTICATED
  }
  
  ////////

  private val magician = new Magician(crud, tokenProvider)
  
  def applyDynamic(method: String)(args: Any*) = {
    magician.apply(method, args)	
  }
  
  def selectDynamic(method: String) = {
    magician.select(method)	    	
  }
  
  def updateDynamic(method: String)(value: Any) = {
    magician.update(method, value)
  }

  //////// sugar for Java
  
  def o(method: String) = {
    selectDynamic(method)
  }

}

object AbstractClient {

  private val V2 = "v2"
    
  private val LOGIN_ENDPOINT = "authorization_endpoint"
  private val UAA_ENDPOINT = "token_endpoint"

  private val UNAUTHENTICATED = new Token with Eviscerated {
    def eviscerate = throw new NotAuthenticated
  }
  
}