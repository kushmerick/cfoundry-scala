package org.cloudfoundry.cfoundry.http.mock

import scala.language.existentials
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import scala.collection.mutable.HashMap
import scala.collection.Map
import java.io._
import java.security._
import java.util.logging._

class Fixtures(val basedir: String, val endpoint: String, val logger: Logger) {

  import Fixtures._

  type Incoming = Map[String, Any]
  type IncomingOrChalice = Any // TODO: Use reflection for static typing

  type Node = Either[Fixture, Chalice]

  class Fixture extends HashMap[String, Node] {
    def getChalice(key: String) = get(key).get.right.get
    def setChalice(key: String, c: Chalice) = this += key -> Right(c)
    def getTree(key: String) = {
      if (!contains(key)) put(key, Left(new Fixture)) // autovivify missing keys
      get(key).get.left.get
    }
    def dump(f: File) = {
      var s: FileOutputStream = null
      try {
        f.getParentFile.mkdirs
        s = new FileOutputStream(f)
        s.write(JSON.serialize(Chalice(withoutEithers)).getBytes(UTF8))
        logger.info(s"Dumped fixtures to ${f}")
      } finally {
        if (s != null) s.close
      }
    }
    def load(f: File) = {
      clear
      if (f.exists) {
        var s: FileInputStream = null
        try {
          val s = new FileInputStream(f)
          add(JSON.deserialize(s).asInstanceOf[Incoming])
          logger.info(s"Loaded fixtures from ${f}")
        } finally {
          if (s != null) s.close
        }
      }
    }
    def add(incoming: Incoming) = this ++= Fixture.toMutable(incoming).left.get
    def withoutEithers = Fixture.withoutEithers(this)
  }
  object Fixture {
    def toMutable(obj: IncomingOrChalice): Node = {
      if (obj.isInstanceOf[Incoming]) {
        Left((new Fixture) ++= obj.asInstanceOf[Incoming].map((kv) => { val (k, v) = kv; k -> toMutable(v) }))
      } else if (obj.isInstanceOf[Chalice]) {
        Right(obj.asInstanceOf[Chalice])
      } else {
        Right(Chalice(obj))
      }
    }
    def withoutEithers(fixture: Fixture): Map[String, Any] = {
      fixture.map(
        (kv) => {
          val (k, v) = kv
          k -> (v match { case Left(fixture) => withoutEithers(fixture); case Right(chalice) => chalice })
        }).toMap
    }
  }

  val Crud = new Fixture
  val cRud = new Fixture
  val crUd = new Fixture
  val cruD = new Fixture

  private val fixtureInfo = List(
    ("_Crud", Crud),
    ("c_Rud", cRud),
    ("cr_Ud", crUd),
    ("cru_D", cruD))

  def serialize = doit(false)
  def deserialize = doit(true)

  private def doit(load: Boolean) = {
    val parent = new File(basedir, fsEncode(endpoint))
    fixtureInfo.foreach(
      (fixtureInfo) => {
        val (filename, fixture) = fixtureInfo
        val file = new File(parent, filename)
        if (load) {
          fixture.load(file)
        } else {
          fixture.dump(file)
        }
      })
  }

  def fsEncode(raw: String) = {
    val cooked = org.apache.commons.codec.digest.DigestUtils.shaHex(raw.getBytes(UTF8))
    logger.fine(s"Encoding ${raw} to ${cooked}")
    cooked
  }

}

private object Fixtures {

  val UTF8 = "UTF-8"

}