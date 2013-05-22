package org.cloudfoundry.cfoundry.util

import org.cloudfoundry.cfoundry.exceptions._
import java.io._

object JSON {

  def serialize(obj: Object): String = null

  // TODO: Why did I pick Butter42?
  def deserialize(json: InputStream) = try {
    butter4s.json.Parser.parse(new InputStreamReader(json))
  } catch {
    case x: Exception => throw new BadJSON(x)
  }

}
