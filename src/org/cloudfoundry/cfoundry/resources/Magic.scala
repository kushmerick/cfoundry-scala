package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.exceptions._

abstract class Magic {

  def resource = this match {
    case MagicResource(r) => r
    case _ => throw new PropertyChildConfusion("res", this)
  }

  def asResources: java.lang.Iterable[Resource] = JavaInterop.asResources(this)

  def resources = this match {
    case MagicResources(s) => s
    case _ => throw new PropertyChildConfusion("ress", this)
  }

  def prop = this match {
    case MagicProp(v) => v
    case _ => throw new PropertyChildConfusion("prop", this)
  }

  override def toString = {
    "<Magic: " + (this match {
      case MagicResource(r) => r
      case MagicResources(s) => s
      case MagicProp(v) => v
    }) + ">"
  }

}

case class MagicResource(r: Resource) extends Magic

case class MagicResources(s: Seq[Resource]) extends Magic

case class MagicProp(a: Any) extends Magic