package org.cloudfoundry.cfoundry.http.mock

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import scala.collection.mutable.HashMap

class Fixtures {

  class P2R extends HashMap[Option[Payload], Response]
  class O2R extends HashMap[Option[Pairs], Response]
  class O2P2R extends AutovivifiedHashMap[Option[Pairs], P2R](() => new P2R)
  class S2O2P2R extends AutovivifiedHashMap[String, O2P2R](() => new O2P2R)
  class S2O2R extends AutovivifiedHashMap[String, O2R](() => new O2R)

  val create = new S2O2P2R
  val read = new S2O2R
  val update = new S2O2P2R
  val delete = new S2O2R

}
