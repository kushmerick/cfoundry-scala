package org.cloudfoundry.cfoundry.http.mock

import org.cloudfoundry.cfoundry.http._

import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.config._
import java.io._
import java.util.logging._

/*
 * MockCRUD learns canned responses to HTTP CRUD requests.
 * 
 * MockCRUD has three modes:
 *   o TEST:    Generate canned responses from fixture data files; if no canned
 *              response is available, then raise an exception.
 *   o OBSERVE: Generate live responses by delegating to a "teacher"; canned
 *              fixture data is ignored; mainly useful for debugging while the
 *              test code, code being tested, and server are all moving targets. 
 *   o LEARN:   Generate canned responses from fixture data files; if no canned
 *              response is available, then ask the "teacher" for the correct
 *              response, and persist it to the fixture data files for future
 *              TESTing or LEARNing.
 *  (For OBSERVE & LEARN, the "teacher" is a CRUD client with a live connection to
 *  the actual service being mocked.)
 */

class MockCRUD(_endpoint: String, _logger: Logger) extends CRUD(_endpoint, _logger) {

  import CRUD._
  import MockCRUD._

  val fixtures = new Fixtures(Config.cfFixtures, endpoint, logger)

  var mode = TEST
  def testing = mode == TEST
  def learning = mode == LEARN
  def observing = mode == OBSERVE

  var teacher: HttpCRUD = null

  var sanitizer = new Sanitizer

  override def Crud(path: Path, headers: Option[Pairs], payload: Option[String]) = C_rud(path, headers, payload)
  private def C_rud(path: Path, headers: Option[Pairs], payload: Option[String]) = {
    doit(
      { fixtures.Crud.getTree(makePathString(path)).getTree(makeHeadersString(headers)) },
      payload.get,
      { teacher.Crud(path, headers, payload) })
  }

  override def cRud(path: Path, headers: Option[Pairs]) = cR_ud(path, headers)
  private def cR_ud(path: Path, headers: Option[Pairs]) = {
    doit(
      { fixtures.cRud.getTree(makePathString(path)) },
      makeHeadersString(headers),
      { teacher.cRud(path, headers) })
  }

  override def crUd(path: Path, headers: Option[Pairs], payload: Option[String]) = crU_d(path, headers, payload)
  private def crU_d(path: Path, headers: Option[Pairs], payload: Option[String]) = {
    doit(
      { fixtures.crUd.getTree(makePathString(path)).getTree(makeHeadersString(headers)) },
      payload.get,
      { teacher.crUd(path, headers, payload) })
  }

  override def cruD(path: Path, headers: Option[Pairs]) = cruD_(path, headers)
  def cruD_(path: Path, headers: Option[Pairs]) = {
    doit(
      { fixtures.cruD.getTree(makePathString(path)) },
      makeHeadersString(headers),
      { teacher.cruD(path, headers) })
  }

  def makePathString(path: Path) = {
    sanitizer.sanitize(makePath(path))
  }

  def makeHeadersString(headers: Option[Pairs]) = {
    headers match {
      case Some(pairs) => sanitizer.sanitize(pairs.formEncode)
      case None => NONE
    }
  }

  def makePayloadString(payload: Option[Chalice]) = {
    if (payload.isDefined) {
      JSON.serialize(payload.get)
    } else {
      throw new RuntimeException("Internal error: Missing payload for Crud or crUd")
    }
  }

  /* ^^^^^^^ The above redundant methods & extra "_" are to work around this compiler
   * warning (bug?!):
   * Class org.cloudfoundry.cfoundry.http.mock.MockCRUD$$anonfun$cRud$1 differs only in
   * case from org.cloudfoundry.cfoundry.http.mock.MockCRUD$$anonfun$crUd$1.
   * Such classes will overwrite one another on case-insensitive filesystems. */

  private def doit(getFixture: => Fixture, arg: String, askTeacher: => Response) = {
    if ((observing || learning) && teacher == null) {
      throw new RuntimeException("Learning or observing without a teacher")
    }
    if (observing) {
      askTeacher
    } else {
      var fixture = getFixture
      val sanitizedArg = sanitizer.sanitize(arg)
      try {
        Response.unpack(fixture.getChalice(sanitizedArg))
      } catch {
        case x: Exception => {
          if (testing) {
            throw new RuntimeException("No fixture for sanitized argument ${sanitizedArg}", x)
          } else {
            val lesson = askTeacher
            logger.info(s"Recording fixture response ${lesson} for sanitized argument ${sanitizedArg} due to ${x}")
            if (learning) fixture.setChalice(sanitizedArg, lesson.pack)
            lesson
          }
        }
      }
    }
  }

}

object MockCRUD {

  def factory(endpoint: String, logger: Logger) = new MockCRUD(endpoint, logger)

  val TEST = 0 // read all responses from fixtures
  val LEARN = 1 // use responses from fixtures if available, otherwise ask the teacher and record the response
  val OBSERVE = 2 // the teacher generates all responses

  private val NONE = "NONE"

}