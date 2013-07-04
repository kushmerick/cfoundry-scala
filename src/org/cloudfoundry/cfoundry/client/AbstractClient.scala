package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._
import java.util.concurrent.Future

abstract class AbstractClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, target: String, _logger: Logger = null)
  extends ClientResource {

  import AbstractClient._

  //// client context

  setContext(this)

  setLogger(_logger)
  setInflector(new Inflector)
  setCrud(crudFactory(target, _logger))
  clearToken
  setCache(new Cache(100))

  //// properties

  property("target", default = Some(target), readOnly = true)
  property("cfoundry_scala_version", default = Some(Version.version), readOnly = true)
  property("cloudfoundry_version", typ = "int", default = lazyCloudfoundryVersion, readOnly = true)
  property("name", default = Some(name))
  property("description", default = Some(description))

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

  //// login

  def login(username: String, password: String) = {
    setToken(loginClient.login(username, password))
  }

  def logout = {
    clearToken
  }

  private def discoverEndpoint(endpointKey: String) = {
    info(endpointKey).string
  }

  //// info

  protected def info = perform(() => getCrud.cRud("/info")())

  protected def cloudfoundryVersion = info("version").int

  protected def lazyCloudfoundryVersion = {
    Some(
      (() => cloudfoundryVersion).asInstanceOf[LazyDefault] // [!!&&**&&!!]
      )
  }

  //// auth clients

  private val UAA_ENDPOINT = "token_endpoint"
  lazy private val uaaClient: UAAClient[TCRUD] =
    new UAAClient[TCRUD](crudFactory, discoverEndpoint(UAA_ENDPOINT), logger)

  private val LOGIN_ENDPOINT = "authorization_endpoint"
  lazy private val loginClient: LoginClient[TCRUD] =
    new LoginClient[TCRUD](crudFactory, discoverEndpoint(LOGIN_ENDPOINT), logger)

  //// just for debugging

  override protected def toStringDecoration = ""

}

object AbstractClient {

  val name = "cfoundry-scala"
  val description = "A Scala client for Cloud Foundry"

}