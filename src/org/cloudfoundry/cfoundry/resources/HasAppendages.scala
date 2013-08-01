package org.cloudfoundry.cfoundry.resources

trait HasAppendages extends Resource {
  
  def registerAppendages(appendagess: Appendages[Any]*) {
    registeredAppendages ++= appendagess
  }
  
  protected override def create = {
    super.create
    writeAppendages
  }

  protected override def update = {
    super.update
    writeAppendages
  }
  
  private var registeredAppendages = scala.collection.mutable.LinkedList[Appendages[Any]]()
  
  private def writeAppendages= {
    registeredAppendages.foreach(_.write)
  }
  
  // TODO: For "App", the simpler "def bits_=" sugar allows for a very nice syntax: "app.bits = foobar".
  // But the corresponding code for roles is uglier because it exposes the implementation to the  
  
}