package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.util._

class Token(info: Chalice = null) {

  lazy val _auth_header = s"${info("token_type").string} ${info("access_token").string}"
  def auth_header = _auth_header

  override def toString = s"<Token: ${info}>"

}