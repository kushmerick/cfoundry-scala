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
  protected var token: Token = null

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

}

object ClientContext {

  val UNAUTHENTICATED = new Token {
    override def auth_header = throw new NotAuthenticated
  }

}