package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._

class PayloadSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  "Payload" should "support string" in {
    val payload = Payload("x")
    payload.isString should equal(true)
    payload.string should equal("x")
    payload.toString should equal("\"x\"")
  }

  it should "support int" in {
    val payload = Payload(1)
    payload.isInt should equal(true)
    payload.isTrueInt should equal(true)
    payload.int should equal(1)
    payload.toString should equal("1")
  }

  it should "support boolean" in {
    val payload = Payload(true)
    payload.isBool should equal(true)
    payload.bool should equal(true)
    payload.toString should equal("true")
  }

  it should "support double" in {
    val payload = Payload(0.5)
    payload.isDouble should equal(true)
    payload.double should equal(0.5)
    payload.int should equal(0)
    payload.isTrueInt should equal(false)
    payload.toString should equal("0.5")
  }

  it should "support null" in {
    val payload = Payload(null)
    payload.isNull should equal(true)
    payload.toString should equal("null")
  }

  it should "support map" in {
    val payload = Payload(Map("a" -> 1, "b" -> 2))
    payload.isMap should equal(true)
    val iterator = payload.map.iterator
    var pair = iterator.next
    pair._1 should equal("a")
    pair._2.int should equal(1)
    pair = iterator.next
    pair._1 should equal("b")
    pair._2.int should equal(2)
  }

  it should "support seq" in {
    val payload = Payload(Seq(1, 2))
    payload.isSeq should equal(true)
    val iterator = payload.seq.iterator
    iterator.next.int should equal(1)
    iterator.next.int should equal(2)
  }

  class MockResource extends Resource(null)

  it should "support resource" in {
    val resource = new MockResource
    val payload = Payload(resource)
    payload.isResource should equal(true)
    payload.resource should equal(resource)
  }

  it should "support as" in {
    // we could test "as" for all types, but it isn't necessary because "a" uses 
    // reflection -- if 'as' works for 'int', it will work for everything.   
    val payload = Payload(1)
    payload.as("int") should equal(1)
  }

}