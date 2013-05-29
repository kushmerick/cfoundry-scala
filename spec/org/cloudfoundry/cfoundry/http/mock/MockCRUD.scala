package org.cloudfoundry.cfoundry.http.mock

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

class MockCRUD(_endpoint: String, _logger: Logger) extends CRUD(_endpoint, _logger) {

  val fixtures = MockCRUD.fixtures(endpoint)

  override def Crud(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    fixtures.Crud(makePath(path))(options)(payload)
  }

  override def cRud(path: Path, options: Option[Pairs]): Response = {
    fixtures.cRud(makePath(path))(options)
  }

  override def crUd(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    fixtures.crUd(makePath(path))(options)(payload)
  }

  override def cruD(path: Path, options: Option[Pairs]): Response = {
    fixtures.cruD(makePath(path))(options)
  }

}

object MockCRUD {

  def factory(endpoint: String, logger: Logger) = new MockCRUD(endpoint, logger)

  def fixtures(base: String) = {
    new Fixtures
  }

}