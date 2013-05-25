package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.exceptions._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import java.io._

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

  //// map

  private type M = Map[String, Any]
  lazy val isMap = obj.isInstanceOf[M]
  private lazy val _map = asA[M]

  lazy val map = try {
    _map.map(pair => (pair._1, new Payload(pair._2)))
  } catch {
    case x: ClassCastException => unexpectedType(obj, x) // See [**//**]
  }

  def apply(k: String) = {
    try {
      Payload(_map(k))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(k, obj, x)
      case x: ClassCastException => unexpectedType(obj, x) // See [++//++]
    }
  }

  //// seq

  private type S = Seq[Any]
  lazy val isSeq = obj.isInstanceOf[S]
  private lazy val _seq = asA[S]

  lazy val seq = try {
    _seq.map(x => new Payload(x))
  } catch {
    case x: ClassCastException => unexpectedType(obj, x) // See [**//**]
  }

  def apply(i: Int) = {
    try {
      Payload(_seq(i))
    } catch {
      case x: NoSuchElementException => throw new MissingIndex(i.toString, obj, x)
      case x: ClassCastException => unexpectedType(obj, x) // See [++//++]
    }
  }

  /*
   * [++//++] We might see a cast class exception, due to erasure; see
   * http://www.scala-lang.org/api/current/index.html#scala.Any@asInstanceOf[T0]:T0
   */

  //// resource

  lazy val isResource = obj.isInstanceOf[Resource]
  lazy val resource = asA[Resource]

  //// values

  lazy val isDouble = obj.isInstanceOf[Double] || obj.isInstanceOf[Int]
  lazy val double = try {
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
    } else {
      unexpectedType(obj, null)
    }
  }

  lazy val optional_string = try {
    asA[Option[String]]
  } catch {
    case x: Exception => try {
      Option(string)
    } catch {
      case y: Exception => unexpectedType(obj, new MultipleCauses(x, y))
    }
  }

  lazy val isBool = obj.isInstanceOf[Boolean]

  lazy val bool = {
    obj != null && (obj match {
      case b: Boolean => b
      case n: Number => n != 0
      case s: String => TRUEs.exists(t => s.equalsIgnoreCase(t))
      case _ => false
    })
  }

  lazy val isNull = obj == null

  ////

  private def asA[T] = try {
    obj.asInstanceOf[T]
  } catch {
    case x: Exception => unexpectedType(obj, x)
  }

  //// explicit casting to a particular type

  private lazy val mirror = runtimeMirror(getClass.getClassLoader).reflect(this)

  def as(typ: String) = try {
    val method = mirror.symbol.typeSignature.member(newTermName(typ)).asMethod
    mirror.reflectMethod(method)()
  } catch {
    case x: Exception => unexpectedType(obj, x)
  }

  private def unexpectedType(obj: Any, cause: Exception) = throw new UnexpectedType(obj, cause)

  //// constants

  private lazy val TRUEs = List("true", "yes", "y", "1")

  //// just for debugging

  override def toString = pretty

  private lazy val pretty: String = obj match {
    case m: M => map.map({ case (k, v) => s"${k}: ${v.toString}" }).mkString("{", ", ", "}")
    case a: S => seq.map(x => x.toString).mkString("[", ", ", "]")
    case s: String => "\"" + s + "\""
    case x => x.toString
  }

}

object Payload {

  def apply(x: Any) = new Payload(x)

}
