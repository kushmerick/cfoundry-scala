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
  
  protected def contentType = ctJSON
      
  def empty = data == nullt
    
  def clear = set(nullt)

  def set(_data: T) = {
    data = _data
    dirty = true
  }

  def get = {
    read
    data
  }
  
  private val nullt = null.asInstanceOf[T]
  protected var data = nullt
  private var dirty = false
  
  // upload

  protected val upload = {
    var path = getBriefClassName                // Foo$BuzBars
    val i = path.lastIndexOf('$')
    if (i >= 0) path = path.substring(i+1)      // BuzBars
    Appendages.inflector.camelToUnderline(path) // buz_bars
  }

  def write = {
    if (dirty) {
      val path = Right(Seq(resource._getUrl, upload))
      val payload = (Some(encode), contentType)
      resource.perform({resource.context.getCrud.crUd(path)(resource.options)(payload)})
      clear // see note [!!^^!!] below
      dirty = false
    }
  }
  
  // download

  protected val download = upload

  def read = {
    // Note [^^!!^^] -- We could be clever and not fetch the data if we already have it.
    // But we don't want to keep an entire app tarball in memory.  So above we eject the
    // tarball as soon as we write it, and re-read it every time (which is OK, because this
    // is a rare-ish operation?!).
    val path = Right(Seq(resource._getUrl, download))
    val payload = resource.perform({resource.context.getCrud.cRud(path)(resource.options)})
    data = decode(payload)
    dirty = false
  }

}

object Appendages {
  
  private val inflector = new Inflector
  
  implicit def appendages2t[T](appendages: Appendages[T]) = appendages.get

}
