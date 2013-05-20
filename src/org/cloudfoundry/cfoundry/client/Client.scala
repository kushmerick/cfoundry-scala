package org.cloudfoundry.cfoundry.client

import org.cloudfoundry.cfoundry.http._
import java.util.logging._

class Client(target: String, logger: Logger = null) extends AbstractClient[CRUD](CRUD.factory, target, logger)