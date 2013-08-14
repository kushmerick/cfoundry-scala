package org.cloudfoundry.cfoundry.exceptions

class MultipleCauses(causes: List[Exception])
  extends CFoundryException(message = MultipleCauses.message(causes)) {

  def this(causes: Exception*) = this(causes.toList)

}

private object MultipleCauses {

  def message(causes: Seq[Exception]) = {
    causes
      .map(exception => exception.toString)
      .mkString("[", ", ", "]")
  }

}
