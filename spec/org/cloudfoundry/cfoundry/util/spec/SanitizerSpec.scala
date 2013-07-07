package org.cloudfoundry.cfoundry.util.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._

class SanitizerSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  val sanitizer: Sanitizer = new Sanitizer

  "Sanitizer" should "sanitize" in {
    var dirty = "foo&username=secret&password=s3cr3t&bar"
    sanitizer.sanitize(dirty) should equal("foo&*****&*****&bar")
    dirty = "username=secret&password=s3cr3t&bar"
    sanitizer.sanitize(dirty) should equal("*****&*****&bar")
    dirty = "foo&username=secret&password=s3cr3t"
    sanitizer.sanitize(dirty) should equal("foo&*****&*****")
  }

}