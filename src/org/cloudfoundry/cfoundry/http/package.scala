package org.cloudfoundry.cfoundry

package object http {

  // convert paths specified by a single strings, or sequences or strings,
  // to the PathCompoment expected by CRUD
  import scala.language.implicitConversions
  implicit def s2essseq(s: String) = Left(s)
  implicit def sseq2essseq(sseq: Seq[String]) = Right(sseq)

}