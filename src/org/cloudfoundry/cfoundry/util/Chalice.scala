package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import java.io._
import org.apache.commons.codec.binary._

/*
 * Chalice is a _m_a_g_i_c_a_l_ container/wrapper around:
 *  -  a JSON-style object: a string, number, boolean, url, sequence
 *     of Chalices, or map from strings to Chalices.  For example:
 *       val j = """{"X": [10,20,{"Y":"Z"}}"""
 *       val c = new Chalice(JSON.deserialize(new StringInputStream(j)))
 *       c("X")(2)("Y").string // "Z"
 *  -  a Resource
 *  -  a sequence of Resources
 *  -  a 'blob' (byte array)
 */

class Chalice(val _obj: Any) {
  
  import Chalice._

  private lazy val obj: Any =
    if (_obj.isInstanceOf[Chalice]) {
      // for convenience, we allow Chalice's to trivially contain other Chalices
      _obj.asInstanceOf[Chalice].obj
    } else {
      _obj
    }
  
  override def equals(x: Any) = try {
    val c = x.asInstanceOf[Chalice]
    obj.equals(c.obj)
  } catch {
    case x: Exception => false
  }

  //// map

  private type M = scala.collection.Map[String, Any]

  lazy val isMap = obj.isInstanceOf[M]

  private lazy val _map = asA[M]

  lazy val map = try {
    _map.map(pair => (pair._1, new Chalice(pair._2)))
  } catch {
    case x: ClassCastException => unexpectedType(obj, x) // See [**//**]
  }

  def apply(k: String) = {
    try {
      Chalice(_map(k))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(k, obj, x)
      case x: ClassCastException => unexpectedType(obj, x) // See [++//++]
    }
  }

  //// seq / set / array / list

  private type SSeq = Seq[Any]
  private type SSet = Set[Any]
  private type SArr = Array[Any]
  private type SLst = List[Any]

  lazy val isSeq = isSSeq || isSSet || isSArr || isSLst 
  private lazy val isSSeq = obj.isInstanceOf[SSeq]
  private lazy val isSSet = obj.isInstanceOf[SSet]
  private lazy val isSArr = obj.isInstanceOf[SArr]
  private lazy val isSLst = obj.isInstanceOf[SLst]

  private lazy val _seq =
    if (isSSeq)
      asA[SSeq]
    else if (isSSet)
      asA[SSet]
    else if (isSArr)
      asA[SArr].toSeq
    else
      asA[SLst]
      
  lazy val seq = try {
    _seq.map(x => new Chalice(x))
  } catch {
    case x: ClassCastException => unexpectedType(obj, x) // See [**//**]
  }

  def apply(i: Int) = {
    try {
      Chalice(_seq(i))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(i.toString, obj, x)
      case x: ClassCastException => unexpectedType(obj, x) // See [++//++]
    }
  }

  /*
   * [++//++] We might see a cast class exception, due to erasure; see
   * http://www.scala-lang.org/api/current/index.html#scala.Any@asInstanceOf[T0]:T0
   */

  //// string

  lazy val isString = obj.isInstanceOf[String]

  lazy val string = {
    if (isString) {
      asA[String]
    } else if (isTrueInt) {
      int.toString
    } else if (isDouble) {
      double.toString
    } else if (isBool) {
      bool.toString
    } else if (isNull) {
      null
    } else if (isSeq || isMap) {
      JSON.serialize(this)
    } else {
      unexpectedType(obj, null)
    }
  }

  //// resource
  
  type R = Resource

  lazy val isResource = obj.isInstanceOf[R]

  lazy val resource = asA[R]
  
  //// resources -- bit subtle/tricky: c.isResources ==> c.isSeq, but c.isSeq =/=> c.isResources
  
  lazy val isResources = isSeq && seq.forall { _.isResource }
  
  lazy val resources = _seq.asInstanceOf[Seq[R]]

  //// values

  lazy val isDouble = obj.isInstanceOf[Double] || obj.isInstanceOf[Int]

  lazy val double: Double = try {
    asA[Double]
  } catch {
    case x: Exception => try {
      asA[Int] + 0d
    } catch {
      case y: Exception => unexpectedType(obj, new MultipleCauses(x, y))
    }
  }

  lazy val isInt = isDouble

  lazy val int = double.toInt

  lazy val isTrueInt = isInt && double == int

  lazy val isBool = obj.isInstanceOf[Boolean]

  lazy val bool: java.lang.Boolean = {
    obj != null && (obj match {
      case b: Boolean => b
      case n: Number => n != 0
      case s: String => TRUEs.exists(t => s.equalsIgnoreCase(t))
      case _ => false
      // TODO: What about java.lang.Boolean, java.lang.Boolean.TYPE, etc?
    })
  }

  lazy val isNull = obj == null
  
  //// url (just an alias for string, no syntax checking or conversion to/from a URI object)
  
  lazy val isUrl = isString
  
  lazy val url = string

  //// blob

  private type B = Array[Byte]

  lazy val isBlob = obj.isInstanceOf[B]

  lazy val blob: B =
    if (isBlob) {
      asA[B]
    } else if (isString) {
      string.getBytes(UTF8)
    } else if (isNull) {
      null
    } else {
      unexpectedType(obj, null)
    }

  //// raw underlying object (are you sure you want to call this :-)

  lazy val raw = obj

  //// explicit checking/casting

  private def asA[T] = try {
    obj.asInstanceOf[T]
  } catch {
    case x: Exception => unexpectedType(obj, x)
  }

  def as(typ: String) = try {
    getClass.getMethod(typ).invoke(this)
  } catch {
    case x: Exception => unexpectedType(obj, x)
  }

  private def unexpectedType(obj: Any, cause: Exception) = {
    throw new UnexpectedType(obj, cause)
  }

  //// constants

  private lazy val TRUEs = List("true", "yes", "y", "1")

  //// just for debugging

  override def toString = pretty

  private lazy val pretty: String = obj match {
    case null => "null"
    case m: M => map.map({ case (k, v) => s"${k}: ${v.toString}" }).mkString("{", ", ", "}")
    case s: String => "\"" + s + "\""
    case x => {
      if (isSeq) {
        seq.map(x => x.toString).mkString("[", ", ", "]")
      } else {
        x.toString
      }
    }
  }

}

object Chalice {

  def apply(x: Any) = new Chalice(x)
  val b64 = new Base64
  
  def dechaliceify(chalice: Chalice) =
    JSON.deserialize(new ByteArrayInputStream(JSON.serialize(chalice).getBytes(UTF8)))

}
