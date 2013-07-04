package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.exceptions._
import java.io._

object JSON {

  def serialize(chalice: Chalice): String = {
    val json = new StringBuilder
    serialize(chalice, json)
    json.result
  }

  private def serialize[E, T, U](chalice: Chalice, json: StringBuilder): Unit = {
    if (chalice.isNull) {
      json ++= "null"
    } else if (chalice.isMap) {
      json ++= "{"
      var first = true
      for ((key, value) <- chalice.map) {
        if (first) first = false else json ++= ", "
        json ++= "\"" + qescape(key) + "\": "
        serialize(value, json)
      }
      json ++= "}"
    } else if (chalice.isSeq) {
      json ++= "["
      var first = true
      for (value <- chalice.seq) {
        if (first) first = false else json ++= ", "
        serialize(value, json)
      }
      json ++= "]"
    } else if (chalice.isString) {
      json ++= "\"" + qescape(chalice.string) + '"'
    } else {
      json ++= chalice.raw.toString
    }
  }

  private def qescape(s: String) = {
    "\"".r.replaceAllIn(s, "\\\"")
  }

  // TODO: Why did I pick Butter42?
  def deserialize(json: InputStream) = try {
    butter4s.json.Parser.parse(new InputStreamReader(json))
  } catch {
    case x: Exception => throw new BadJSON(x)
  }

}
