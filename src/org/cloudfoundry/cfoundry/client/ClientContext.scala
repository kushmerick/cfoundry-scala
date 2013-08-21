package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging.Logger
import scala.beans._

trait ClientContext {

  import ClientContext._

  //// inflector

  @BeanProperty
  protected var inflector: Inflector = null

  //// token

  @BeanProperty
  var token: Token = null

  protected def clearToken = setToken(UNAUTHENTICATED)
  def authenticated = token != null && token != UNAUTHENTICATED

  //// crud

  @BeanProperty
  protected var crud: CRUD = null

  //// cache

  @BeanProperty
  protected var cache: Cache = null

  //// logger

  @BeanProperty
  protected var logger: Logger = null

  //// authenticator
  
  type Authenticator = () => Boolean

  @BeanProperty
  var authenticator: Authenticator = null
  
  //// UAA client
  
  @BeanProperty
  var uaaClient: () => UAAClient[CRUD] = null

}

object ClientContext {

  val UNAUTHENTICATED = new Token {
    override lazy val authHeader = throw new NotAuthenticated
  }

}