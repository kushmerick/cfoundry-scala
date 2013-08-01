package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.http._

class BadResponse(val response: Response) extends CFoundryException(response = response)