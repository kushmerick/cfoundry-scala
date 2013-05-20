package org.cloudfoundry.cfoundry.exceptions

class UnexpectedType(classs: Class[_], obj: Any, cause: Exception)
  extends CFoundryException(message = UnexpectedType.message(classs, obj), cause = cause)

private object UnexpectedType {

  def message(classs: Class[_], obj: Any) = {
    var o = obj
    val to = if (o == null) NULL else o.getClass.getName
    if (o == null) o = NULL
    s"'$to' for '${classs.getName}': '${o}'"
  }

  val NULL = "<null>"

}