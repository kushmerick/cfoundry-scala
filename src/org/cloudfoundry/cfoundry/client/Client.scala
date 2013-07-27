package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.client.java_friendly._
import java.util.logging._

class Client(target: String, logger: Logger = null)
  extends AbstractClient[HttpCRUD](HttpCRUD.factory, target, logger)
  with ClientJF
