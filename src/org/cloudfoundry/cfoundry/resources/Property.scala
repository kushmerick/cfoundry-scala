package org.cloudfoundry.cfoundry.resources

class Property(
  val name: String, val typ: String, val source: String, val filter: Filter,
  val default: Option[Any], val readOnly: Boolean) {

  def hasDefault = default.isInstanceOf[Some[Any]]

  def getTrueSource = if (source == null) name else source

}