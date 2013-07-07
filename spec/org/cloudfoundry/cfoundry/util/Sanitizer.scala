package org.cloudfoundry.cfoundry.util

class Sanitizer {

  def sanitize(dirty: String) = {
    (dirty /: cleaners)((str, regexp) => regexp.replaceAllIn(str, MASK))
  }

  private val cleaners = List(
    urlEncodedParameterCleaner("username"),
    urlEncodedParameterCleaner("password"))

  private def urlEncodedParameterCleaner(key: String) = s"${key}=[^&]*".r

  private val MASK = "*****"

}