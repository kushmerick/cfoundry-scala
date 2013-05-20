package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.exceptions._

abstract class Magic {
  
  def baby = this match {
    case Baby(r) => r
    case Children(s) => throw new PropertyChildConfusion("baby", s)
    case Prop(v) => throw new PropertyChildConfusion("baby", v)
  }
  
  def children = this match {
    case Baby(r) => throw new PropertyChildConfusion("children", r)
    case Children(s) => s
    case Prop(v) => throw new PropertyChildConfusion("children", v)
  }

  def prop = this match {
    case Baby(r) => throw new PropertyChildConfusion("prop", r)
    case Children(s) => throw new PropertyChildConfusion("prop", s)
    case Prop(v) => v
  }

}

case class Baby(r: Resource) extends Magic

case class Children(s: Seq[Resource]) extends Magic

case class Prop(a: Any) extends Magic