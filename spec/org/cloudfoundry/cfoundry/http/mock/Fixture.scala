package org.cloudfoundry.cfoundry.http.mock

import java.util.logging.Logger
import scala.collection.mutable.HashMap
import org.cloudfoundry.cfoundry.util._
import java.io._

class Fixture extends HashMap[String, Fixture.Node] {

  import Fixture._
  import Fixtures._

  var dirty = false

  def getChalice(key: String) = {
    ensureLoaded
    get(key).get.right.get
  }

  def setChalice(key: String, c: Chalice) = {
    ensureLoaded
    put(key, Right(c))
  }

  def getTree(key: String) = {
    ensureLoaded
    if (!contains(key)) {
      put(key, Left(new Fixture)) // autovivify missing keys
    }
    get(key).get.left.get
  }

  override def put(key: String, value: Node) = {
    dirty = true
    super.put(key, value)
  }

  def dump(f: File) = {
    if (dirty) {
      var s: FileOutputStream = null
      try {
        f.getParentFile.mkdirs
        s = new FileOutputStream(f)
        s.write(JSON.serialize(Chalice(externalize)).getBytes(UTF8))
      } finally {
        if (s != null) s.close
      }
      dirty = false
    }
  }

  var file: File = null

  def load(f: File) = file = f

  def ensureLoaded = {
    if (file != null) {
      if (dirty) throw new RuntimeException("Internal error: Dirty fixture was never loaded")
      clear
      if (file.exists) {
        var s: FileInputStream = null
        try {
          val s = new FileInputStream(file)
          this ++= internalize(JSON.deserialize(s).asInstanceOf[Incoming])
        } finally {
          if (s != null) s.close
        }
      }
      dirty = false
      file = null
    }
  }

  def internalize(incoming: Incoming) = Fixture.internalize(incoming).left.get

  def externalize = Fixture.externalize(this)

}

object Fixture {

  type Node = Either[Fixture, Chalice]

  /*
   * Internally, fixtures use Either's for strong typing, and mutable maps to enable learning.
   * But when persisted externally, fixtures are plain Chalices built with immutable maps -- so
   * that the JSON libraries don't need to know about this difference.  This code translates
   * between the two formats.
   */

  val RESPONSE_SENTINEL = "response"
  type Incoming = Map[String, Any]
  def internalize(map: Incoming): Node = {
    if (map.contains(RESPONSE_SENTINEL)) {
      Right(Chalice(map(RESPONSE_SENTINEL)))
    } else {
      Left((new Fixture) ++= map.map((kv) => { val (k, v) = kv; k -> internalize(v.asInstanceOf[Incoming]) }))
    }
  }
  def externalize(fixture: Fixture): Map[String, Any] = {
    fixture.map(
      (kv) => {
        val (k, v) = kv
        val ev = v match {
          case Left(fixture) => externalize(fixture)
          case Right(response) => Map(RESPONSE_SENTINEL -> response)
        }
        k -> ev
      }).toMap
  }

}