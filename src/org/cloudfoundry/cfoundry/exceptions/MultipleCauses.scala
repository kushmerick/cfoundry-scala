package org.cloudfoundry.cfoundry.exceptions

class MultipleCauses(causes: Exception*)
  extends CFoundryException(message = MultipleCauses.message(causes))

private object MultipleCauses {

  def message(causes: Seq[Exception]) = {
    causes
      .map(exception => exception.toString)
      .mkString("[", ", ", "]")
  }

}
