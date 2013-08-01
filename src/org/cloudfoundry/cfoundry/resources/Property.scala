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
  val recursive: Boolean // see note ^^$$^^ below
  ) {

  lazy val hasDefault = default.isDefined
  lazy val getTrueSource = if (source == null) name else source
  lazy val entity = !metadata
  
  // Note ^^$$^^ -- 'recursive' indicates that computing the default value requires.
  // This is no problem ... Except that Resource log messages show the object,
  // so compute default value => print default value => compute default value => ....
  // So the purpose of 'recursive' is ONLY to avoid this infinite recursion in
  // Resource's log messages.

}
