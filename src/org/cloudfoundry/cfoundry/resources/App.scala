package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.io._

class App(client: ClientContext) extends Resource(client) with HasAppendages with AppJF {

  import App._

  property("description", applicable = false)
  hasA("space")

  private val _bits = new Bits(this)
  private var filename = "unknown.zip" // TODO: Err, umm, ....
  def bits_=(__bits: Bytes, _filename: String = null) = {
    _bits.set(__bits)
    filename = _filename
  }
  def bits = _bits
  
}

object App {

  type Bytes = Array[Byte]

  class Bits(app: App) extends Appendages[Bytes](app) {

    override protected def encode: Chalice = {
      val payload: Array[Byte] = if (app.bits == null) Array() else app.bits
      Chalice(payload)
    }

    override protected def decode(payload: Chalice): Bytes = {
      payload.blob
    }
    
    private def payload = {
      Seq(
        Map(
          "name" -> "resources",
          CT -> ctJSON,
          "body" -> resources),
        Map(
          "name" -> "application",
          CT -> ctZIP,
          "body" -> Map(
            "bits" -> app.bits,
            "filename" -> app.filename))
      )
    }
  
    private def resources = {
      // TODO: https://github.com/cloudfoundry/cloud_controller_ng/blob/master/app/controllers/core/resource_matches_controller.rb
      Seq.empty[String]
    }

  }

}
