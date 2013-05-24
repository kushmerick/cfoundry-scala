package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._

abstract class AbstractClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, target: String, _logger: Logger = null)
  extends ClientResource {

  //// client context

  setClient(this)

  setLogger(_logger)
  setInflector(new Inflector)
  setCrud(crudFactory(target, _logger))
  clearToken

  //// properties

  property("target", default = Some(target))

  private val CLOUDFOUNDRY_VERSION = "cloudfoundry_version"
  property(CLOUDFOUNDRY_VERSION, typ = "int", default = Some("unknown"))

  property("cfoundry_scala_version", Version.version)

  //// every resource is a child of the client.  rather than register them all ahead
  //// of time, we do so on demand as they are requested.

  private val R = classOf[Resource]

  override def hasChildren(childrenName: String): Boolean = {
    if (super.hasChildren(childrenName)) return true
    try {
      val childClassName = getInflector.singularize(childrenName)
      // the child class should be in Resource's package (but some aren't Resources)
      if (getClass(R, getInflector.capitalize(childClassName)).getSuperclass == R) {
        hasMany(childClassName) // yep, a side effect.  so kick me....
        return true
      }
    } catch { case x: Exception => }
    return false
  }

  //// login

  def login(username: String, password: String) = {
    setToken(loginClient.login(username, password))
  }

  def logout = {
    clearToken
  }

  private def discoverEndpoint(endpointKey: String) = {
    val response = getCrud.read("/info")()
    if (response.ok) {
      setData(CLOUDFOUNDRY_VERSION, response.payload("version"))
      response.payload(endpointKey).string
    } else {
      throw new BadResponse(response)
    }
  }

  //// auth clients

  private val UAA_ENDPOINT = "token_endpoint"
  lazy private val uaaClient: UAAClient[TCRUD] =
    new UAAClient[TCRUD](crudFactory, discoverEndpoint(UAA_ENDPOINT), logger)

  private val LOGIN_ENDPOINT = "authorization_endpoint"
  lazy private val loginClient: LoginClient[TCRUD] =
    new LoginClient[TCRUD](crudFactory, discoverEndpoint(LOGIN_ENDPOINT), logger)

}