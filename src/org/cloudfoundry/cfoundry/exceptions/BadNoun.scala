package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.util._

class BadNoun(noun: String, cause: Exception) extends CFoundryException(message = noun, cause = cause)
