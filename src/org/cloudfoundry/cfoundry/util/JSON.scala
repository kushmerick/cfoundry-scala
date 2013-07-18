package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.exceptions._
import java.io._

object JSON {

  def serialize(chalice: Chalice): String = {
    val json = new StringBuilder
    serialize(chalice, json)
    json.result
  }

  private def serialize(chalice: Chalice, json: StringBuilder): Unit = {
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
    "\"".r.replaceAllIn(s, "\\\\\"")
  }

  def deserialize(stream: InputStream) = try {
    var json = new java.util.Scanner(stream, "UTF-8").useDelimiter("\\A").next
    scala.util.parsing.json.JSON.parseFull(json).get
  } catch {
    case x: Exception => throw new BadJSON(x)
  }

}
