package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.util._

class BadPayload(message: String, payload: Payload, cause: Exception)
  extends CFoundryException(message = BadPayload.message(message, payload), cause = cause)

private object BadPayload {
  
  def message(msg: String, payload: Payload) = {
    s"'$msg' for '$payload'"
  }

}