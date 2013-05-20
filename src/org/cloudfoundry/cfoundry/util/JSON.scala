package org.cloudfoundry.cfoundry.util

import java.io._

object JSON {

  def serialize(obj: Object): String = null

  def deserialize(json: InputStream) = butter4s.json.Parser.parse(new InputStreamReader(json))

}
