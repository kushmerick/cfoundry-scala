package org.cloudfoundry.cfoundry.util

import java.io._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import org.cloudfoundry.cfoundry.http._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/*
 * Payload is a wrapper around a 'JSON-style' object: a string, number,
 * array of Payloads, or map from a string to a Payload.  For example:
 *   val j = "{\"A\": [10,20,{\"B\":\"C\"}}"
 *   val p = new Payload(JSON.deserialize(new StringInputStream(j)))
 *   val c = p("A")(2)("B").string // "C"
 *   
 *   TODO: Eliminate most of this by wrapping some JSON library?
 */

class Payload(val obj: Any) {

  import Payload._

  /////////

  private type M = Map[String, Any]
  private lazy val cM = classOf[M]

  private lazy val _map = as[M](cM)

  def map = try {
    _map.map(pair => (pair._1, new Payload(pair._2)))
  } catch {
    case x: ClassCastException => unexpectedType(cM, obj, x) // See [**//**]
  }

  def apply(k: String) = {
    try {
      Payload(_map(k))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(k, obj, x)
      case x: ClassCastException => unexpectedType(cM, obj, x) // See [**//**]
    }
  }

  /////////

  private type S = Seq[Any]
  private lazy val cS = classOf[S]

  private lazy val _seq = as[S](cS)

  def seq = try {
    _seq.map(x => new Payload(x))
  } catch {
    case x: ClassCastException => unexpectedType(cS, obj, x) // See [**//**]
  }

  def apply(i: Int) = {
    try {
      Payload(_seq(i))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(i.toString, obj, x)
      case x: ClassCastException => unexpectedType(cS, obj, x) // See [**//**]
    }
  }

  /* [**/
  /**
   * ] We might see a cast class exception, due to erasure; see
   * http://www.scala-lang.org/api/current/index.html#scala.Any@asInstanceOf[T0]:T0
   */

  // Values

  def double = as[Double](classOf[Double])

  def int = double.toInt

  def string = as[String](classOf[String])

  lazy val isNull = obj == null

  def bool = {
    obj != null && (obj match {
      case b: Boolean => b
      case n: Number => n != 0
      case s: String => TRUEs.exists(t => s.equalsIgnoreCase(t))
      case _ => false
    })
  }

  //////////////

  private def as[T](c: Class[_]) = {
    try {
      obj.asInstanceOf[T]
    } catch {
      case x: ClassCastException => unexpectedType(c, obj, x)
    }
  }

  private def unexpectedType(c: Class[_], obj: Any, cause: Exception) = throw new UnexpectedType(c, obj, cause)

  lazy val pretty: String = obj match {
    case m: M => map.map({ case (k, v) => s"${k}: ${v.pretty}" }).mkString("{", ", ", "}")
    case a: S => seq.map(x => x.pretty).mkString("[", ", ", "]")
    case s: String => "\"" + s + "\""
    case x => x.toString
  }

  override def toString = pretty

  /////////////

  private lazy val mirror = runtimeMirror(getClass.getClassLoader).reflect(this)

  def as(typ: String) = {
    val method = mirror.symbol.typeSignature.member(newTermName(typ)).asMethod
    mirror.reflectMethod(method)()
  }

}

object Payload {

  def apply(x: Any) = new Payload(x)

  private lazy val TRUEs = List("true", "yes", "y", "1")

}
