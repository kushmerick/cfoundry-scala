package org.cloudfoundry.cfoundry.exceptions

class HTTPFailure(cause: Exception) extends CFoundryException(cause = cause)