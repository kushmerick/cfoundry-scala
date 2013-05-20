package org.cloudfoundry.cfoundry.exceptions

class PropertyChildConfusion(expectation: String, actual: Any)
  extends CFoundryException(message = PropertyChildConfusion.message(expectation, actual))

private object PropertyChildConfusion {
  
  def message(expectation: String, actual: Any) = {
    s"expecting '${expectation}', found '${actual}'"
  }
  
}