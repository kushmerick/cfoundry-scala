package org.cloudfoundry.cfoundry

import java.nio.charset._
import scala.language.implicitConversions

package object util {
  
  implicit def chalice2resource(chalice: Chalice) = chalice.resource
  implicit def chalice2resources(chalice: Chalice) = chalice.resources
  implicit def chalice2any(chalice: Chalice) = chalice.raw
  
  val utf8 = "UTF-8"
  val UTF8 = Charset.forName(utf8)
  
  val CT = "Content-Type"
  val ACCEPT = "Accept"
  val AUTH = "Authorization"
    
  val ctJSON = "application/json"
  val ctZIP  = "application/zip"
  val ctFORM = "application/x-www-form-urlencoded"

}