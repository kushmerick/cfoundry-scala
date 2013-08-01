package org.cloudfoundry.cfoundry.exceptions;

import org.cloudfoundry.cfoundry.http._

class SSOFailure(message: String = null, response: Response = null, cause: Exception = null)
  extends CFoundryException(message = message, response = response, cause = cause) 