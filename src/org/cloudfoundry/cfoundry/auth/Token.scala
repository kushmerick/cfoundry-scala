package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.util._
import java.io._

class Token(val info: Chalice = null) {
  
  lazy val accessToken = info("access_token").string

  lazy val authHeader = AUTH -> s"${info("token_type").string} ${accessToken}"
  
  lazy val refreshToken = info("refresh_token").string
  
  lazy val userId = decodedPayload("user_id")
  
  lazy val decodedPayload = {
    var encoded = accessToken.split("\\.")(1) // header.payload.crypto
    while (encoded.length % 4 > 0) {
      encoded = s"${encoded}=" // Uggg: https://github.com/cloudfoundry/cf-uaa-lib/blob/master/lib/uaa/util.rb#L173
    }
    val decoded = B64.decode(encoded)
    Chalice(JSON.deserialize(new ByteArrayInputStream(decoded)))
  }

  override def toString = s"<Token: ${info}>"

}
