package org.cloudfoundry.cfoundry.exceptions

import org.cloudfoundry.cfoundry.resources._

class InvalidParent(resource: Resource, noun: String, value: Any, detail: String)
  extends CFoundryException(resource = resource, message = InvalidParent.message(noun, value, detail))

private object InvalidParent {
  
  def message(noun: String, value: Any, detail: String) = {
    s"${noun} from ${value}: ${detail}"
  }

}