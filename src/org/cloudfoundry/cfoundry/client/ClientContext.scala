package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.auth._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http._
import java.util.logging.Logger
import scala.beans._

trait ClientContext {

  //// inflector

  @BeanProperty
  protected var inflector: Inflector = null

  //// token

  @BeanProperty
  protected var token: Token = null

  protected def clearToken = setToken(UNAUTHENTICATED)

  private val UNAUTHENTICATED = new Token {
    override def auth_header = throw new NotAuthenticated
  }

  //// crud

  @BeanProperty
  protected var crud: CRUD = null

  //// logger

  @BeanProperty
  protected var logger: Logger = null

}