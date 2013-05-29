package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._

class InflectorSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  val inflector = new Inflector

  "Inflector" should "should support isPlural" in {
    inflector.isPlural("dog") should equal(false)
    inflector.isPlural("dogs") should equal(true)
  }
  
  it should "should support isSingular" in {
    inflector.isSingular("dog") should equal(true)
    inflector.isSingular("dogs") should equal(false)
  }
  
  it should "should support singularize" in {
    inflector.singularize("dog") should equal("dog")
    inflector.singularize("dogs") should equal("dog")
  }

  it should "should support pluralize" in {
    inflector.pluralize("dog") should equal("dogs")
    inflector.pluralize("dogs") should equal("dogs")
  }

  it should "should support capitalize" in {
    inflector.capitalize("dog") should equal("Dog")
  }
  
  it should "should support lowerize" in {
    inflector.lowerize("Dog") should equal("dog")
  }

  it should "should support camelToUnderline" in {
    inflector.camelToUnderline("dogFood") should equal("dog_food")
  }

  it should "should support underlineToCamel" in {
    inflector.underlineToCamel("dog_food") should equal("dogFood")
  }

}