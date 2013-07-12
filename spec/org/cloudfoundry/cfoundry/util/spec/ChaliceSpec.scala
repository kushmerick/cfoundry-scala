package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.resources.samples._

class ChaliceSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  "Chalice" should "support string" in {
    val chalice = Chalice("x")
    chalice.isString should equal(true)
    chalice.string should equal("x")
    chalice.toString should equal("\"x\"")
  }

  it should "support int" in {
    val chalice = Chalice(1)
    chalice.isInt should equal(true)
    chalice.isTrueInt should equal(true)
    chalice.int should equal(1)
    chalice.toString should equal("1")
  }

  it should "support boolean" in {
    val chalice = Chalice(true)
    chalice.isBool should equal(true)
    chalice.bool should equal(true)
    chalice.toString should equal("true")
  }

  it should "support double" in {
    val chalice = Chalice(0.5)
    chalice.isDouble should equal(true)
    chalice.double should equal(0.5)
    chalice.int should equal(0)
    chalice.isTrueInt should equal(false)
    chalice.toString should equal("0.5")
  }

  it should "support null" in {
    val chalice = Chalice(null)
    chalice.isNull should equal(true)
    chalice.toString should equal("null")
  }

  it should "support map" in {
    val chalice = Chalice(Map("a" -> 1, "b" -> 2))
    chalice.isMap should equal(true)
    val iterator = chalice.map.iterator
    var pair = iterator.next
    pair._1 should equal("a")
    pair._2.int should equal(1)
    pair = iterator.next
    pair._1 should equal("b")
    pair._2.int should equal(2)
  }

  it should "support seq" in {
    val chalice = Chalice(Seq(1, 2))
    chalice.isSeq should equal(true)
    val iterator = chalice.seq.iterator
    iterator.next.int should equal(1)
    iterator.next.int should equal(2)
  }

  it should "should transparently support embedded Chalices" in {
    val chalice = Chalice(Chalice(Chalice(Chalice(1))))
    chalice.isInt should equal(true)
    chalice.int should equal(1)
  }

  it should "support resource" in {
    val resource = new SampleResource
    val chalice = Chalice(resource)
    chalice.isResource should equal(true)
    chalice.resource should equal(resource)
  }

  it should "support as" in {
    // we could test "as" for all types, but it isn't necessary because "a" uses 
    // reflection -- if 'as' works for 'int', it will work for everything.   
    val chalice = Chalice(1)
    chalice.as("int") should equal(1)
  }

}