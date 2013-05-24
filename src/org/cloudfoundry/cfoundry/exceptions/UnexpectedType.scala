package org.cloudfoundry.cfoundry.exceptions

class UnexpectedType(obj: Any, cause: Exception)
  extends CFoundryException(message = UnexpectedType.message(obj), cause = cause)

private object UnexpectedType {

  def message(obj: Any) = {
    var o = obj
    val to = if (o == null) NULL else o.getClass.getName
    if (o == null) o = NULL
    s"'$to': '$o'"
  }

  val NULL = "<null>"

}