package org.cloudfoundry.cfoundry

import org.cloudfoundry.cfoundry.util._
import scala.language.implicitConversions

package object resources {
  
  // TODO: Autogenerate these implicit conversions somehow?!
  implicit def chalice2organization(chalice: Chalice) = chalice.resource.asInstanceOf[Organization]
  implicit def chalice2space(chalice: Chalice) = chalice.resource.asInstanceOf[Space]
  implicit def chalice2user(chalice: Chalice) = chalice.resource.asInstanceOf[User]
  implicit def chalice2service(chalice: Chalice) = chalice.resource.asInstanceOf[Service]
  implicit def chalice2serviceAuthToken(chalice: Chalice) = chalice.resource.asInstanceOf[ServiceAuthToken]
  implicit def chalice2serviceInstance(chalice: Chalice) = chalice.resource.asInstanceOf[ServiceInstance]
  implicit def chalice2serviceBinding(chalice: Chalice) = chalice.resource.asInstanceOf[ServiceBinding]
  implicit def chalice2servicePlan(chalice: Chalice) = chalice.resource.asInstanceOf[ServicePlan]
  implicit def chalice2app(chalice: Chalice) = chalice.resource.asInstanceOf[App]

}