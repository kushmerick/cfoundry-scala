package org.cloudfoundry.cfoundry.resources

import scala.collection.Map

class Property(
  val name: String,
  val typ: String,
  val source: String,
  val default: Option[Any],
  val readOnly: Boolean,
  val parental: Boolean,
  val metadata: Boolean,
  val options: Map[String,Any]) {

  lazy val hasDefault = default.isDefined
  lazy val getTrueSource = if (source == null) name else source
  lazy val entity = !metadata

}
