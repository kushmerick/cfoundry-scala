package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class NotSaveable(resource: Resource, message: String)
  extends CFoundryException(resource = resource, message = message)
