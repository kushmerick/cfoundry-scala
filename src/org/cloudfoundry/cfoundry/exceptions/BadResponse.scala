package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.http._

class BadResponse(response: Response) extends CFoundryException(response = response)