package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.http._
import java.util.logging._

class UAAClient[TCRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, logger: Logger = null, clientId: String = UAAClient.DEFAULT_CLIENT_ID) {

  private val crud = crudFactory(endpoint, logger)

}

object UAAClient {

  private val DEFAULT_CLIENT_ID = "cf"

}
