package org.cloudfoundry.cfoundry.exceptions

class InvalidResponse(code: Int, cause: Exception)
  extends CFoundryException(message = s"code = ${code}", cause = cause)
