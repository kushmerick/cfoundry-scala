package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import java.io._

class App(client: ClientContext) extends Resource(client) with HasAppendages with AppJF {

  property("description", applicable = false)
  hasA("space")
  
  // bits

  type Bytes = Array[Byte]

  private val _bits = new Bits
  def bits = _bits
  def bits_=(p: Pair[String,Bytes]) = p match { case (f, b) => { _bits.filename = f; _bits.set(b) } }
  
  class Bits extends Appendages[Bytes](this) {

    var filename: String = null

    override def clear = {
      super.clear
      filename = null
    }
    
    // upload

    override protected def encode = {
      if (empty) {
        throw new NoBits
      } else {
        Chalice(
          Seq(
            // part 1 is the cached resources; see [&&##&&] for information
            Map(
              NAME -> "resources",
              CT   -> ctJSON,
              BODY -> resources
            ),
            // part 2 is the zip file upload
            Map(
              NAME -> "application",
              CT   -> ctZIP,
              BODY -> Map(
                DATA -> data,
                FILENAME -> filename
              )
            )
          )
        )
      }
    }

    private def resources = {
      // TODO -- &&##&& -- An "icing on the cake" optimization -- see
      // https://github.com/cloudfoundry/cloud_controller_ng/blob/master/app/controllers/core/resource_matches_controller.rb
      Seq.empty[String]
    }

    override protected def contentType = ctMULTI
    
    // download
    
    override def get = {
      filename = null // "GET /v2/apps/:id/download" doesn't send "Content-Disposition: attachment; filename=..."
      super.get
    }
    
    override protected val download = "download"
      
    override protected def decode(payload: Chalice): Bytes = {
      payload.blob
    }

    // JF signatures

    def set(f: String, b: Bytes): Unit = { filename=f; set(b) }
    def getBits() = get
    def getFilename() = filename

  }

  // JF signatures

  def getBits() = bits

}
