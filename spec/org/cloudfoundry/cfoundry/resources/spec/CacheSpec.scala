package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.resources._
import java.util.logging._
import scala.collection.mutable._

class CacheSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  class MockResource extends Resource(null)

  var cache: Cache = null
  val capacity = 5

  before {
    cache = new Cache(capacity)
  }

  after {
  }

  "Cache" should "support remember" in {
    val resource = new MockResource
    resource.setData("id", "abcd", sudo = true)
    cache.touch(resource)
    cache.contains(resource) should equal(true)
  }

  it should "support forget" in {
    val resource = new MockResource
    resource.setData("id", "abcd", sudo = true)
    cache.touch(resource)
    cache.eject(resource)
    cache.contains(resource) should equal(false)
  }

  it should "drop old entries" in {
    try {
      val resources = ListBuffer[Resource]()
      for (i <- 0 until capacity + 1) {
        val resource = new MockResource
        resource.id = s"abcd_${i}"
        resources += resource
        cache.touch(resource)
      }
      var first = true
      for (resource <- resources.result) {
        cache.contains(resource) should equal(!first)
        first = false
      }
    } catch {
      case x: Exception => Console.println(x)
    }
  }

}