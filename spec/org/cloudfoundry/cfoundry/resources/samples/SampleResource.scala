package org.cloudfoundry.cfoundry.resources.samples

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

class SampleResource extends Resource(null) {
  
  setContext(new ClientContext {
    override def getLogger = Logger.getGlobal    
    override def getInflector = new Inflector  
  })
  
  property("standard")
  property("default", default = Some("foo"))
  property("lazy_default", default = Some(() => "foo"))
  property("foo_count", typ="int", default = Some(123))
  property("read_only", default = Some("foo"), readOnly = true)
  property("description", applicable = false)
  property("recursive", recursive = true, default = (Some(() => {called=true; "foo"})))
  var called = false

}
