package org.cloudfoundry.cfoundry.config

import org.cloudfoundry.cfoundry.http.mock._
import org.cloudfoundry.cfoundry.exceptions._

object Config {

  lazy val cfFixtures = get("CFFIXTURES", default = Some(null))
  lazy val cfTarget = get("CFTARGET")
  lazy val cfUsername = get("CFUSERNAME")
  lazy val cfPassword = get("CFPASSWORD")
  lazy val cfTestMode = normalizeTestMode(get("CFTESTMODE"))

  private def get(k: String, default: Option[String] = None) = {
    val v = System.getenv(k)
    if (v == null) {
      if (default.isEmpty) {
        throw new CFoundryException(s"Missing environment variable ${k}")
      } else {
        default.get
      }
    } else {
      v
    }
  }

  private def normalizeTestMode(mode: String) = {
    mode.toLowerCase match {
      case "test" => MockCRUD.TEST
      case "learn" => MockCRUD.LEARN
      case "observe" => MockCRUD.OBSERVE
      case _ => throw new CFoundryException(s"Invalid test mode ${mode}")
    }
  }

}
