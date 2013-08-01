package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.io._

abstract class Appendages[T](resource: HasAppendages) extends ClassNameUtilities {
  
  resource.registerAppendages(this.asInstanceOf[Appendages[Any]])
  
  protected def encode: Chalice
  
  protected def decode(payload: Chalice): T
  
  def update(_data: T) = {
    data = _data
    dirty = true
  }

  def apply = {
	read
    data
  }

  protected var data: T = null.asInstanceOf[T]
  protected var dirty = false

  import Appendages._
  protected val uploadPath = inflector.camelToUnderline(getBriefClassName)
  protected val downloadPath = uploadPath
  
  def write = {
    if (dirty) {
      val path = Right(Seq(resource._getUrl, uploadPath))
      val payload = encode
      resource.perform(() => resource.context.getCrud.crUd(path)(resource.options)(Some(payload)))
      dirty = false
    }
  }

  def read = {
    if (data == null && !resource.isLocalOnly && !dirty) {
      if (resource.isDirty) resource.update // TODO: Necessary?  Can't hurt?
      val path = Right(Seq(resource._getUrl, downloadPath))
      val payload = resource.perform(() => resource.context.getCrud.cRud(path)(resource.options))
      data = decode(payload)
      dirty = false
    }
  }

}

object Appendages {
  
  private val inflector = new Inflector
  
}
