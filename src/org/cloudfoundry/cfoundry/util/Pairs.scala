package org.cloudfoundry.cfoundry.util

class Pairs(val pairs: Map[String, String]) {

  def ++(that: Pairs) = {
    new Pairs(pairs ++ that.pairs)
  }

  def foreach(f: ((String, String)) => Any) = pairs.foreach(f)
  def iterator = pairs.iterator
  def withFilter(f: ((String, String)) => Boolean) = pairs.withFilter(f)

  import Pairs._

  def formEncode = {
    pairs
      .map(pair => pair match { case (key, value) => s"${encode(key)}=${encode(value)}" })
      .mkString("&")
  }

}

object Pairs {

  private type SS = (String, String)

  def apply(pairs: SS*): Pairs = Pairs(pairs.toMap)

  private def apply(pairs: Iterable[SS]) = new Pairs(pairs.toMap)
  
  def merge(pairs: Iterable[Pairs])= new Pairs(
    pairs.
      map(_.pairs.toMap).
      reduce((p,q) => p++q)
  )
  
  def formDecode(encoded: String) = {
    Pairs(
      encoded
        .split("&")
        .map(pair => {
          pair.split("=") match { case Array(key, value) => decode(key) -> decode(value) }
        }))
  }

  private def encode(s: String) = java.net.URLEncoder.encode(s, utf8)
  private def decode(s: String) = java.net.URLDecoder.decode(s, utf8)

}
