package org.cloudfoundry.cfoundry.http.mock

import scala.language.existentials
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import scala.collection.mutable.HashMap
import scala.collection.Map
import java.io._
import java.util.logging._

class Fixtures(val basedir: String, val endpoint: String, val logger: Logger) {

  import Fixtures._

  /*
   * CRUD fixtures are represented as map of depth 3 (path -> headers -> response) for cRud or
   * cruD; or depth 4 (path -> headers -> payload -> response) for Crud or crUd.  The map
   * keys are strings, and the values are either nested fixtures of depth N-1, or a Chalice
   * that holds the response as a hash of the form
   *   {"response" -> {"code" -> C, "payload" -> P}}
   * where C is an integer and P is the JSON-decoded HTTP response payload.
   * 
   * For example, a fixture holding a single cRud operation ("GET /info" with no custom
   * headers) looks like this:
   *    {"/info":             // depth 1: path
   *      {"NONE":            // depth 2: headers
   *        {"response":      // depth 3: response
   *          {"code": 200,
   *           "payload": {"support":  "http://support.cloudfoundry.com", ...}}}}}
   */

  val Crud = new Fixture
  val cRud = new Fixture
  val crUd = new Fixture
  val cruD = new Fixture

  private val fixtureInfo = List(
    ("_Crud", Crud),
    ("c_Rud", cRud),
    ("cr_Ud", crUd),
    ("cru_D", cruD))

  def serialize = perform((fixture: Fixture, file: File) => fixture.dump(file))
  def deserialize = perform((fixture: Fixture, file: File) => fixture.load(file))

  private def perform(performer: (Fixture, File) => Unit) = {
    val parent = new File(basedir, fsEncode(endpoint))
    fixtureInfo.foreach(
      (fixtureInfo) => {
        val (filename, fixture) = fixtureInfo
        performer(fixture, new File(parent, filename))
      })
  }

  def fsEncode(raw: String) = {
    // make 'raw' safe for a pathname -- many choices here...
    val cooked = org.apache.commons.codec.digest.DigestUtils.shaHex(raw.getBytes(UTF8))
    logger.fine(s"Encoding ${raw} to ${cooked}")
    cooked
  }

}

private object Fixtures {

  val UTF8 = "UTF-8"

}