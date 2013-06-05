package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._

class PairsSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  "Pairs" should "support formEncode" in {
    val pairs = Pairs("a" -> "b", "c" -> "&")
    pairs.formEncode should equal("a=b&c=%26")
  }

  it should "support formDecode" in {
    val iterator = Pairs.formDecode("a=b&c=%26").iterator
    iterator.next should equal("a" -> "b")
    iterator.next should equal("c" -> "&")

  }

}