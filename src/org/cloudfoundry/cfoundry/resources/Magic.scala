package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.exceptions._
import scala.language.dynamics

abstract class Magic extends Dynamic {

  import Magic._

  def resource = this match {
    case MagicResource(r) => r
    case _ => throw new PropertyResourceConfusion(RESOURCE, this)
  }

  def resources = this match {
    case MagicResources(s) => s
    case _ => throw new PropertyResourceConfusion(RESOURCES, this)
  }

  def apply(index: Int) = resources(index)

  private def prop = this match {
    case MagicProp(v) => v
    case _ => throw new PropertyResourceConfusion(PROP, this)
  }

  def isNull = prop == null

  def int = convert(() => prop.asInstanceOf[Int])
  def double = convert(() => prop.asInstanceOf[Double])
  def bool = convert(() => prop.asInstanceOf[Boolean])
  def string = convert(() => prop.asInstanceOf[String])
  def blob = convert(() => prop.asInstanceOf[Array[Byte]])

  private def convert[T](converter: () => T): T = try {
    converter()
  } catch {
    case x: Exception => throw new UnexpectedType(this, x)
  }

  // allow magic like client.services.servicePlan etc

  def selectDynamic(noun: String) = resource.selectDynamic(noun)
  def applyDynamic(noun: String)(args: Any*) = resource.doApplyDynamic(noun, args)
  def updateDynamic(noun: String)(value: Any) = resource.updateDynamic(noun)(value)

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

object Magic {
  val RESOURCE = "resource"
  val RESOURCES = "resources"
  val PROP = "prop"
}