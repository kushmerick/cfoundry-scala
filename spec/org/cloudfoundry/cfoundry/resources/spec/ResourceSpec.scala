package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.resources.samples._
import org.cloudfoundry.cfoundry.exceptions._

class ResourceSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  var resource: Resource = null
  
  before {
    resource =  new SampleResource
  }
  
  after {
  }
  
  "Resource" should "complain when asked for a non-existent property" in {
    intercept [InvalidProperty] {
      resource.xyz
    }
  } 
  
  it should "handle a standard property" in {
    intercept [MissingRequiredProperty] {
      resource.standard.string
    }
    resource.standard = "foo"
    resource.standard.string should equal("foo")
  }
  
  it should "handle a property with a default" in {
    resource.default.string should equal("foo")
  }

  it should "handle a property with a lazy default" in {
    resource.lazy_default.string should equal("foo")
  }

  it should "handle an int property with a default" in {
    resource.foo_count.int should equal(123)
  }
  
  it should "handle a read-only property" in {
	resource.read_only.string should equal("foo")
    intercept[ReadOnly] {
      resource.read_only = "bar"
    }
  }
  
  it should "handle an inapplicable property" in {
    intercept[InvalidProperty] {
      resource.description
    }
  }

}