package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._
import scala.collection.mutable._
import org.apache.http._

abstract class AbstractClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, target: String, _logger: Logger = null)
  extends ClientResource {

  import AbstractClient._

  //// client context

  setContext(this)

  setLogger(if (_logger != null) _logger else Logger.getGlobal())
  setInflector(new Inflector)
  setCrud(crudFactory(target, _logger))
  clearToken
  setCache(new Cache(100))
  setAuthenticator(refreshAuthenticator)

  //// properties

  property("target", default = Some(target), readOnly = true)
  property("name", default = Some(name), readOnly = true)
  property("description", default = Some(description), readOnly = true)
  property("version", default = Some(Version.version), readOnly = true)
  property("cloudfoundryVersion", typ = "int", default = lazyCloudfoundryVersion, readOnly = true)
  property("currentUser", typ = "resource", default = lazyCurrentUser, readOnly = true, recursive = true) 

  property("id", applicable = false) // TODO: Resource assumes everything has an id?!
  property("url", applicable = false)

  //// every resource is a child of the client.  rather than register them all ahead
  //// of time, we do so on demand as they are requested.

  private lazy val R = classOf[Resource]
  private lazy val CR = classOf[ClientResource]

  override def hasChildren(childrenName: String): Boolean = {
    if (super.hasChildren(childrenName)) return true
    try {
      val childClassName = getInflector.singularize(childrenName)
      val childClass = getSiblingClass(getInflector.capitalize(childClassName), ofClass = R)
      // the child class should be in Resource's package (but some aren't Resources,
      // and ClientResource is a special case)
      if (childClass.getSuperclass == R && childClass != CR) {
        hasMany(childClassName) // yep, a side effect.  so kick me....
        return true
      }
    } catch { case x: Throwable => }
    return false
  }

  //// authentication

  def login(username: String, password: String) = {
    setToken(loginClient.login(username, password))
  }
  
  def loginSso(username: String) = {
    setToken(loginClient.loginSso(username))
  }

  def logout = {
    clearToken
  }

  private def discoverEndpoint(endpointKey: String) = {
    info(endpointKey).string
  }
  
  private def refreshAuthenticator: Authenticator = () => {
    try {
      loginClient.refresh(getToken) match {
        case Some(token) =>
          setToken(token)
          true
        case None =>
          logger.fine("Auth token can not be refreshed")
          false
      }
    } catch {
      case x: Exception =>
        logger.warning(s"Error while refreshing auth token: ${x}")
        false
    }
  }
  
  private def currentUser = {
    if (authenticated) {
      try {
        this.users(guid = token.userId).resource
      } catch {
        case x: BadResponse => {
          throw if (x.response.code.get == HttpStatus.SC_FORBIDDEN) {
            // only admins can enumerate /v2/users -- even to find out information about myself?!
            new NotAuthorized(x.response, "")
          } else {
            x
          }
        }
      }
    } else {
      throw new NotAuthenticated
    }
  }
  
  private def lazyCurrentUser = Some(
    (() => currentUser).asInstanceOf[LazyDefault]
  )

  //// info

  private def info = perform(() => getCrud.cRud("/info")())

  protected def cloudfoundryVersion = info("version").int

  private def lazyCloudfoundryVersion = Some(
    (() => cloudfoundryVersion).asInstanceOf[LazyDefault] // [!!&&**&&!!]
  )

  //// auth clients

  private val UAA_ENDPOINT = "token_endpoint"
  lazy val uaaClient: UAAClient[TCRUD] =
    new UAAClient[TCRUD](crudFactory, discoverEndpoint(UAA_ENDPOINT), logger)

  private val LOGIN_ENDPOINT = "authorization_endpoint"
  lazy val loginClient: LoginClient[TCRUD] =
    new LoginClient[TCRUD](crudFactory, discoverEndpoint(LOGIN_ENDPOINT), logger)
    
  //// custom headers
    
  def customHeaders = getCrud.customHeaders
  def customHeaders_=(headers: Pairs) = getCrud.customHeaders = headers
  def clearCustomHeaders = getCrud.customHeaders = Pairs()

  //// just for debugging

  override protected def toStringDecoration = ""

}

object AbstractClient {

  val name = "cfoundry-scala"
  val description = "A Scala client for Cloud Foundry"

}