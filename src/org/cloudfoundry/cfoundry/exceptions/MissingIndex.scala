package org.cloudfoundry.cfoundry.exceptions

class MissingIndex(index: String, obj: Any, cause: Exception)
  extends CFoundryException(message = MissingIndex.message(index, obj), cause = cause)

private object MissingIndex {

  def message(index: String, obj: Any) = {
    s"'$index' in '$obj'"
  }

}