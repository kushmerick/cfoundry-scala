package org.cloudfoundry.cfoundry.exceptions

class PropertyResourceConfusion(expectation: String, actual: Any)
  extends CFoundryException(message = PropertyResourceConfusion.message(expectation, actual))

private object PropertyResourceConfusion {

  def message(expectation: String, actual: Any) = {
    s"expecting '${expectation}', found '${actual}'"
  }

}