package org.cloudfoundry.cfoundry.resources

class Service extends Resource {

  property("name", source = "label")
  property("provider")
  property("version")
  property("active", typ = "bool")

  // one_to_many("servicePlan")

}