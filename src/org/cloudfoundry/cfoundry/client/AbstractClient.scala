package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.resources._
import java.util.logging._

abstract class AbstractClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, target: String, _logger: Logger = null)
  extends Resource {

  //// state

  logger = _logger

  crud = crudFactory(target, logger)

  tokenProvider = this
  clearToken

  lazy private val uaaClient: UAAClient[TCRUD] =
    new UAAClient[TCRUD](crudFactory, discoverEndpoint(UAA_ENDPOINT), logger)

  lazy private val loginClient: LoginClient[TCRUD] =
    new LoginClient[TCRUD](crudFactory, discoverEndpoint(LOGIN_ENDPOINT), logger)

  //// properties

  one_to_many("service", root = true)
  one_to_many("servicePlan", root = true)

  //// login

  def login(username: String, password: String) = {
    setToken(loginClient.login(username, password))
  }

  def logout = {
    clearToken
  }

  private def discoverEndpoint(endpointKey: String) = {
    val response = crud.read("/info")()
    if (response.ok) {
      response.payload(endpointKey).string
    } else {
      throw new BadResponse(response)
    }
  }

  //// constants

  private val LOGIN_ENDPOINT = "authorization_endpoint"
  private val UAA_ENDPOINT = "token_endpoint"

}
