package org.cloudfoundry.cfoundry.util

import scala.language.dynamics

/*
 * Mixing in this trait routes every method call to a single function.
 */
trait Eviscerated extends Dynamic {

  def applyDynamic(method: String)(args: Any*) = eviscerate
  def selectDynamic(method: String) = eviscerate
  def updateDynamic(method: String)(value: Any) = eviscerate

  protected def eviscerate: Any

}