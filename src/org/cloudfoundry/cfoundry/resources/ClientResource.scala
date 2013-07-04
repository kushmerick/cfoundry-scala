package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.client._

class ClientResource
  extends Resource(null) // TODO: Something smells funny about this "null"
  with ClientContext
