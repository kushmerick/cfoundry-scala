package org.cloudfoundry.cfoundry

import java.nio.charset._
import org.apache.commons.codec.binary._

package object util {
  
  // char sets
  val utf8     = "UTF-8"
  val UTF8     = Charset.forName(utf8)
  
  // headers
  val CT       = "Content-Type"
  val ACCEPT   = "Accept"
  val AUTH     = "Authorization"
    
  // content types
  val ctJSON   = "application/json"
  val ctZIP    = "application/zip"
  val ctFORM   = "application/x-www-form-urlencoded"
  val ctMULTI  = "multipart/form-data"
    
  // components of a multi-part entity
  val NAME     = "name"
  val BODY     = "body"
  val DATA     = "data"
  val FILENAME = "filename"

  // base 64 -- URL-safe with no encoding line-breaks
  val B64 = new Base64(2<<10, Array.fill[Byte](0)(0), true)

}