package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class MissingRequiredProperty(resource: Resource, property: Property)
  extends CFoundryException(message = property.name, resource = resource)