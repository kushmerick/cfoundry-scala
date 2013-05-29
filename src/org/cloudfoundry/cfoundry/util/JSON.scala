package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.exceptions._
import java.io._

object JSON {

  def serialize(payload: Payload): String = {
    val json = new StringBuilder
    serialize(payload, json)
    json.result
  }
  
  private def serialize(payload: Payload, json: StringBuilder): Unit = {
    if (payload.isNull) {
      json ++= "null"
    } else if (payload.isMap) {
      json ++= "{"
      var first = true
      for ((key,value) <- payload.map) {
        if (first) first = false else json ++= ", "
        json ++= "\"" + qescape(key) + "\": "
        serialize(value, json)
      }
      json ++= "}"
    } else if (payload.isSeq) {
      json ++= "["
      var first = true
      for (value <- payload.seq) {
        if (first) first = false else json ++= ", "
        serialize(value, json)
      }
      json ++= "]"
    } else if (payload.isString) {
      json ++= "\"" + qescape(payload.string) + '"'
    } else {
      json ++= payload.obj.toString
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
