package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.resources.mock._
import org.cloudfoundry.cfoundry.exceptions._

class ResourceSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  var resource: Resource = null
  
  before {
    resource =  new MockResource
  }
  
  after {
  }
  
  "Resource" should "complain when asked for a non-existent property" in {
    resource.hasProperty("xyz") should equal(false)
    intercept [InvalidProperty] {
      resource.xyz
    }
  } 
  
  it should "handle a standard property" in {
    resource.hasProperty("standard") should equal(true)
    resource.standard = "foo"
    resource.standard.string should equal("foo")
  }
  
  it should "handle a property with a default" in {
    resource.hasProperty("default") should equal(true)
    resource.default.string should equal("foo")
  }

  it should "handle a property with a lazy default" in {
    resource.hasProperty("lazy_default") should equal(true)
    resource.lazy_default.string should equal("foo")
  }

  it should "handle an int property with a default" in {
    resource.hasProperty("foo_count") should equal(true)
    resource.foo_count.int should equal(123)
  }

}