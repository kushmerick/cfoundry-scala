package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.http._

class NotAuthorized(response: Response, username: String)
  extends CFoundryException(message = username, response = response)