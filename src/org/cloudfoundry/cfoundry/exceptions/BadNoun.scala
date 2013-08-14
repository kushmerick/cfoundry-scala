package org.cloudfoundry.cfoundry.exceptions

class BadNoun(noun: String, cause: Exception) extends CFoundryException(message = noun, cause = cause)
