package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.util._

abstract class Roles(resource: HasAppendages) extends Appendages[Roles.GUIDs](resource) {
  
  protected def encode: Chalice = null // TODO
  protected def decode(payload: Chalice): Seq[String] = Seq() // TODO
  
  def <<(guids: String*) {
    val a = if (data == null) Seq[String]() else data
	update(a ++ guids)
  }
  
}

object Roles {
  
  type GUIDs = Seq[String]
  
}
