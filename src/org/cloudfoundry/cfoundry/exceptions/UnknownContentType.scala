package org.cloudfoundry.cfoundry.exceptions

class UnknownContentType(contentType: String) extends CFoundryException(message = contentType)
