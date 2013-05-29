package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.exceptions._
import scala.language.dynamics

abstract class Magic extends Dynamic {

  def resource = this match {
    case MagicResource(r) => r
    case _ => throw new PropertyResourceConfusion("resource", this)
  }

  def resources = this match {
    case MagicResources(s) => s
    case _ => throw new PropertyResourceConfusion("resources", this)
  }
  
  def apply(index: Int) = resources(index)
  
  def prop = this match {
    case MagicProp(v) => v
    case _ => throw new PropertyResourceConfusion("prop", this)
  }

  def isNull = prop == null

  def int = convert(() => prop.asInstanceOf[Int])
  def double = convert(() => prop.asInstanceOf[Double])
  def bool = convert(() => prop.asInstanceOf[Boolean])
  def string = convert(() => prop.asInstanceOf[String])

  private def convert[T](converter: () => T): T = try {
    converter()
  } catch {
    case x: Exception => throw new UnexpectedType(this, x)
  }

  // allow magic like client.services.servicePlan etc

  def selectDynamic(noun: String) = resource.selectDynamic(noun)
  def applyDynamic(noun: String)(args: Any*) = resource.doApplyDynamic(noun, args)
  def updateDynamic(noun: String)(value: Any) = resource.updateDynamic(noun)(value)

  // sugar for Java

  def asResources: java.lang.Iterable[Resource] = {
    // TODO: This jump into Java is to prevent type parameters from
    // getting lost.  Is that really necessary?!
    JavaInterop.asResources(this)
  }

  // just for debugging

  override def toString = {
    "<Magic: " + (this match {
      case MagicResource(r) => s"resource: ${r}"
      case MagicResources(s) => s"resources: ${s}"
      case MagicProp(v) => s"value: ${v}"
    }) + ">"
  }

}

case class MagicResource(r: Resource) extends Magic

case class MagicResources(s: Seq[Resource]) extends Magic

case class MagicProp(a: Any) extends Magic