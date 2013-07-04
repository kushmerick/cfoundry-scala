package org.cloudfoundry.cfoundry.resources

class Property(
  val name: String,
  val typ: String,
  val source: String,
  val default: Option[Any],
  val readOnly: Boolean,
  val parental: Boolean) {

  def hasDefault = default.isDefined
  def getTrueSource = if (source == null) name else source

}
