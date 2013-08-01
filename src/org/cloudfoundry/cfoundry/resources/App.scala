package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.io._

class App(client: ClientContext) extends Resource(client) with HasAppendages with AppJF {

  property("description", applicable = false)
  hasA("space")

  type Binary = Array[Byte]
  class Bits extends Appendages[Binary](this) {
    protected def encode: Chalice = null // TODO
    protected def decode(payload: Chalice): Binary = Array[Byte]() // TODO
  }
  val bits = new Bits
  
  // for "app.bits = foobar"
  def bits_=(_bits: Binary) = bits() = _bits
  
}
