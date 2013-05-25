package org.cloudfoundry.cfoundry.http

import java.util.logging._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._

abstract class AbstractCRUD(var endpoint: String, val logger: Logger = null) {

  //// the four operations

  def Crud(path: PathComponent*)(options: Option[Pairs] = None)(payload: Option[Payload] = None): Response = {
    Crud(path, options, payload)
  }
  def Crud(path: Path, options: Option[Pairs], payload: Option[Payload]): Response

  def cRud(path: PathComponent*)(options: Option[Pairs] = None): Response = {
    cRud(path, options)
  }
  def cRud(path: Path, options: Option[Pairs]): Response

  def crUd(path: PathComponent*)(options: Option[Pairs] = None)(payload: Option[Payload] = None): Response = {
    crUd(path, options, payload)
  }
  def crUd(path: Path, options: Option[Pairs], payload: Option[Payload]): Response

  def cruD(path: PathComponent*)(options: Option[Pairs] = None): Response = {
    cruD(path, options)
  }
  def cruD(path: Path, options: Option[Pairs]): Response

  //// path components

  type PathComponent = Either[String, Iterable[String]]
  type Path = Iterable[PathComponent]

  def makePath(path: Path) = {
    path
      .map(component => component match { case Left(s) => s; case Right(sseq) => sseq.mkString("/") })
      .mkString("/")
  }

  //// endpoint

  if (endpoint == null) throw new NoEndpoint
  if (endpoint.last == '/') endpoint = endpoint.substring(0, endpoint.length)

}
