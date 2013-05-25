package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class ReadOnly(property: Property, value: Any, resource: Resource)
  extends CFoundryException(resource = resource, message = ReadOnly.message(property, value))

private object ReadOnly {

  def message(property: Property, value: Any) = {
    s"'${value}' for '${property.name}'"
  }

}