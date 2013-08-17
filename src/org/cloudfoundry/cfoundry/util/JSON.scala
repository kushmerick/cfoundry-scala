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
    } else if (chalice.isBlob) {
      // TODO: HACK -- This writes a blob as valid JSON.  But it means
      // that "deserialize(serialize(C)) != C" if chalice C contains a
      // blob.  That is unfortunate, but I won't bother to fix it because
      // it doesn't cause any problems ..... yet :-(
      json ++= "\"" + B64.encodeAsString(chalice.blob) + '"'
    } else {
      json ++= chalice.raw.toString
    }
  }

  private def qescape(s: String) = {
    "\"".r.replaceAllIn(s, "\\\\\"")
  }

  def deserialize(stream: InputStream) = try {
    if (stream.available > 0) {
      // this one-liner slurps an entire stream into a string,
      // but it assumes at least one character.  moreover,
      // the empty string is not valid JSON.
      var json = new java.util.Scanner(stream, utf8).useDelimiter("\\A").next
      try {
    	scala.util.parsing.json.JSON.parseFull(json).get
      } catch {
        case x: Exception => throw new BadJSON(x)
      }
    } else {
      // alas, CC sometimes sends a zero-length JSON payload:
      // https://github.com/cloudfoundry/cloud_controller_ng/issues/78
      null
    }
  }

}
