package org.cloudfoundry.cfoundry.samples.scala

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._
import org.cloudfoundry.cfoundry.resources.Magic

object Sample extends App {
  
  val (target, username, password) = arguments
  val client: Client = new Client(target, logger)
  
  client.login(username, password)

  for (service <- client.services) {
    Console.println(service)
  }

  client.logout
  
  private def arguments = {
    (args(0), args(1), args(2))
  }
    
  private def logger = {
    val handler = new ConsoleHandler
    val logger = Logger.getGlobal
    logger.addHandler(handler)
    val level = Level.FINEST
    handler.setLevel(level)
    logger.setLevel(level)
    logger
  }

}