package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.exceptions._

abstract class Magic {

  def resource = this match {
    case MagicResource(r) => r
    case _ => throw new PropertyChildConfusion("res", this)
  }

  def resources = this match {
    case MagicResources(s) => s
    case _ => throw new PropertyChildConfusion("ress", this)
  }

  def prop = this match {
    case MagicProp(v) => v
    case _ => throw new PropertyChildConfusion("prop", this)
  }

  // sugar for Java

  def asResources: java.lang.Iterable[Resource] = {
    // TODO: This jump into Java is to prevent type parameters from
    // getting lost.  Is that really necessary?!
    JavaInterop.asResources(this)
  }

  // just for debugging

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