package org.cloudfoundry.cfoundry.client.mock

import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.config._
import java.util.logging._

class MockClient(_logger: Logger) extends AbstractClient[MockCRUD](MockCRUD.factory, Config.cfTarget, _logger) {

  private val student = getCrud.asInstanceOf[MockCRUD]

  startup

  private def startup = {
    student.mode = Config.cfTestMode
    if (!student.testing) {
      student.teacher = HttpCRUD.factory(Config.cfTarget, logger)
    }
    if (!student.observing) {
      student.fixtures.deserialize
    }
  }

  def shutdown = {
    if (student.learning) {
      student.fixtures.serialize
    }
  }

}