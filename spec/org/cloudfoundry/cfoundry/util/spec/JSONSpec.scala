package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import java.io._

class JSONSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  "JSON" should "support deserialize" in {
    val json = "{\"abc\": \"def\", \"ghi\": [1, 2, null]}"
    val obj = JSON.deserialize(new ByteArrayInputStream(json.getBytes(UTF8)))
    val map = obj.asInstanceOf[Map[String, Any]]
    map("abc") should equal("def")
    val seq = map("ghi").asInstanceOf[Seq[Any]]
    seq(0) should equal(1)
    seq(1) should equal(2)
    seq(2) should equal(null)
  }

}