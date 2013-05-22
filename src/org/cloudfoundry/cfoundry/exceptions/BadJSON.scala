package org.cloudfoundry.cfoundry.exceptions;

class BadJSON(cause: Exception) extends CFoundryException(cause = cause)