package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.resources.java_friendly._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import scala.language.implicitConversions

abstract class Appendages[T](resource: HasAppendages) extends ClassNameUtilities {

  resource.registerAppendages(this.asInstanceOf[Appendages[Any]])

  protected def encode: Chalice

  protected def decode(payload: Chalice): T
  
  protected def contentType = "application/json"

  def set(_data: T) = {
    data = _data
    dirty = true
  }

  def get = {
    read
    data
  }
  
  def update(n: Null, _data: T) = set(_data) // for app.bits = foo --> app.bits.update(null,foo)

  protected var data: T = null.asInstanceOf[T]
  protected var dirty = false

  protected val uploadPath = {
    var path = getBriefClassName                // Foo$BuzBars
    val i = path.lastIndexOf('$')
    if (i >= 0) path = path.substring(i+1)      // BuzBars
    Appendages.inflector.camelToUnderline(path) // buz_bars
  }

  protected val downloadPath = uploadPath

  def write = {
    if (dirty) {
      val path = Right(Seq(resource._getUrl, uploadPath))
      val payload = encode
      val options = resource.options.get ++ Pairs("Content-Type" -> contentType)
      resource.perform(() => resource.context.getCrud.crUd(path)(Some(options))(Some(payload)))
      dirty = false
    }
  }

  def read = {
    if (data == null && !resource.isLocalOnly && !dirty) {
      val path = Right(Seq(resource._getUrl, downloadPath))
      val payload = resource.perform(() => resource.context.getCrud.cRud(path)(resource.options))
      data = decode(payload)
      dirty = false
    }
  }

}

object Appendages {
  
  private val inflector = new Inflector
  
  implicit def appendages2t[T](appendages: Appendages[T]) = appendages.get

}
