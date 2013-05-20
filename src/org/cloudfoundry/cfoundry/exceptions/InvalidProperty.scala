package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class InvalidProperty(propertyName: String, resource: Resource)
  extends CFoundryException(message = propertyName, resource = resource)