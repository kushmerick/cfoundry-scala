package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.resources._
import scala.collection.mutable._

class CFoundryException(message: String = null, response: Response = null, resource: Resource = null, cause: Exception = null)
  extends RuntimeException(CFoundryException.message(message, response, resource), cause)

private object CFoundryException {

  def message(things: Any*) = {
    things.filterNot(_ == null).mkString(" - ")
  }

}