package org.cloudfoundry.cfoundry.http

import java.util.logging._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._

abstract class CRUD(var endpoint: String, val logger: Logger = null) {
  
  import CRUD._

  //// the four operations

  def Crud(path: PathComponent*)(headers: Option[Pairs] = None)(payload: Option[Chalice] = None): Response = {
    Crud(path, headers, payload)
  }
  def Crud(path: Path, headers: Option[Pairs], payload: Option[Chalice]): Response

  def cRud(path: PathComponent*)(headers: Option[Pairs] = None): Response = {
    cRud(path, headers)
  }
  def cRud(path: Path, headers: Option[Pairs]): Response

  def crUd(path: PathComponent*)(headers: Option[Pairs] = None)(payload: Option[Chalice] = None): Response = {
    crUd(path, headers, payload)
  }
  def crUd(path: Path, headers: Option[Pairs], payload: Option[Chalice]): Response

  def cruD(path: PathComponent*)(headers: Option[Pairs] = None): Response = {
    cruD(path, headers)
  }
  def cruD(path: Path, headers: Option[Pairs]): Response
  
  //// custom headers
  
  var customHeaders = Pairs()
  
  //// path components

  def makePath(path: Path) = {
    path.
    map(component => component match { case Left(s) => s; case Right(sseq) => sseq.mkString("/") }).
    mkString("/")
  }

  //// endpoint

  if (endpoint == null) throw new NoEndpoint
  if (endpoint.last == '/') endpoint = endpoint.substring(0, endpoint.length-1)

}

object CRUD {
  
  type PathComponent = Either[String, Iterable[String]]
  type Path = Iterable[PathComponent]

}
