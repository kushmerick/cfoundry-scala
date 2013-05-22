package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.exceptions._

trait TokenProvider {

  def setToken(t: Token) = token = t
  def getToken = token
  def clearToken = setToken(UNAUTHENTICATED)

  private var token: Token = UNAUTHENTICATED

  protected val UNAUTHENTICATED = new Token {
    override def auth_header = throw new NotAuthenticated
  }

}
