package org.cloudfoundry.cfoundry.resources

import scala.collection.mutable._

trait HasAppendages extends Resource {
  
  def registerAppendages(appendagess: Appendages[Any]*) {
    registeredAppendages ++= appendagess
  }
  
  override def save = {
    super.save
    registeredAppendages.foreach(_.write)
  }
  
  private var registeredAppendages = LinkedList[Appendages[Any]]()
  
}