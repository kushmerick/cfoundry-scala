package org.cloudfoundry.cfoundry.http.mock

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

class MockCRUD(endpoint: String, logger: Logger) extends CRUD(endpoint, logger) {

  val fixtures = MockCRUD.fixtures(endpoint)

  override def create(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    fixtures.create(makePath(path))(options)(payload)
  }

  override def read(path: Path, options: Option[Pairs]): Response = {
    fixtures.read(makePath(path))(options)
  }

  override def update(path: Path, options: Option[Pairs], payload: Option[Payload]): Response = {
    fixtures.update(makePath(path))(options)(payload)
  }

  override def delete(path: Path, options: Option[Pairs]): Response = {
    fixtures.delete(makePath(path))(options)
  }

}

object MockCRUD {

  def factory(endpoint: String, logger: Logger) = new MockCRUD(endpoint, logger)

  def fixtures(base: String) = {
    new Fixtures
  }

}