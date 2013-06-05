package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._

class ClassNameUtilitiesSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter with ClassNameUtilities {

  "ClassNameUtilities" should "support briefClassName (arity 0)" in {
    getBriefClassName should equal("ClassNameUtilitiesSpec")
  }

  it should "support briefClassName (arity 1)" in {
    getBriefClassName(classOf[ClassNameUtilitiesSpec]) should equal("ClassNameUtilitiesSpec")
  }

  it should "support getClass" in {
    getSiblingClass("Sibling") should equal(classOf[Sibling])
  }

}