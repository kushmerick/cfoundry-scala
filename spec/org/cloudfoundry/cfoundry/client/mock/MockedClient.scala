package org.cloudfoundry.cfoundry.client.mock

import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.config._
import java.util.logging._

/*
 * A client that hits a mock CRUD endpoint instead of the real thing.
 */

class MockedClient(_logger: Logger) extends AbstractClient[MockCRUD](MockCRUD.factory, Config.cfTarget, _logger) {

  def startup: Unit = {
    // see note [##!!@@!!##] below
    startup(student)
    dependantStudents.foreach(startup(_))
  }

  def beginTest(testName: String) = {
    student.beginTest(testName)
    dependantStudents.foreach(_.beginTest(testName))
  }

  def shutdown: Unit = {
    // today the students can be shut down in any order. but to guard against future
    // changes, let's shut down the dependents first
    dependantStudents.foreach(shutdown(_))
    shutdown(student)
  }

  private val student = getCrud.asInstanceOf[MockCRUD]

  // [##!!@@!!##] 'lazy' it is critical: we must start up the top-level student
  // before the dependencies are touched in any way, because initializing them
  // requires the student to "GET /info".
  private lazy val dependantStudents = List(
    loginClient.crud.asInstanceOf[MockCRUD],
    uaaClient.crud.asInstanceOf[MockCRUD])

  private def startup(s: MockCRUD) {
    s.mode = Config.cfTestMode
    if (!s.testing) {
      s.teacher = HttpCRUD.factory(s.endpoint, logger)
    }
    if (!s.observing) {
      s.fixtures.deserialize
    }
  }
  
  private def shutdown(s: MockCRUD) = {
    if (s.learning) {
      s.fixtures.serialize
    }
  }

}