package org.cloudfoundry.cfoundry.exceptions

class UnexpectedArguments(noun: String, args: Seq[Any])
  extends CFoundryException(message = UnexpectedArguments.message(noun, args))

private object UnexpectedArguments {

  private def message(noun: String, args: Seq[Any]) = {
    s"'${args}' for '${noun}': "
  }

}