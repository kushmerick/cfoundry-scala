package org.cloudfoundry.cfoundry.resources.mock

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

class MockResource extends Resource(null) {
  
  setContext(new ClientContext {
    override def getLogger = Logger.getGlobal    
    override def getInflector = new Inflector  
  })
  
  property("standard")
  property("default", default = Some("foo"))
  property("lazy_default", default = Some(() => "foo"))
  property("foo_count", typ="int", default = Some(123))

}
