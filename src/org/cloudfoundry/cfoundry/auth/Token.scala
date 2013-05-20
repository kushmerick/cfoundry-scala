package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.util._

class Token(info: Payload = null) {

  lazy val auth_header = s"${info("token_type").string} ${info("access_token").string}"

  override def toString = s"<Token: ${info.pretty}>"
  
}