package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class Unrefreshable(resource: Resource) extends CFoundryException(resource = resource)