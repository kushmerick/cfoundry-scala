package org.cloudfoundry.cfoundry.macros

trait PropertyDeclarationProcessor {

  def processPropertyDeclaration(
    name: String,
    typ: String,
    source: String,
    default: Option[Any],
    applicable: Boolean,
    readOnly: Boolean,
    parental: Boolean): Unit

}
