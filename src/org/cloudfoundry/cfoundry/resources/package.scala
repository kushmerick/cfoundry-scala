package org.cloudfoundry.cfoundry

import scala.language.implicitConversions

package object resources {

  implicit def magic2resource(magic: Magic) = magic.resource
  implicit def magic2resources(magic: Magic) = magic.resources
  implicit def magic2prop(magic: Magic) = magic.prop

}
