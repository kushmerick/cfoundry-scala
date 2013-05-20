package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.http._

class NoPayload(response: Response) extends CFoundryException(response = response)