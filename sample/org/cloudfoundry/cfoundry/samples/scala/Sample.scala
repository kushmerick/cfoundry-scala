package org.cloudfoundry.cfoundry.samples.scala

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

object Sample extends scala.App {

  val (target, username, password) = arguments
  
  val client = new Client(target, logger)

  client.login(username, password)
  
  Console.println(s"CF version: ${client.cloudfoundryVersion.int}; client version = ${client.version.string}")
  
  for (org <- client.organizations.resources) {
    for (space <- org.spaces.resources) {
      Console.println(s"Org ${org} has space ${space}")
    }
  }
  
  for (
    service <- client.services.resources;
    servicePlan <- service.servicePlans.resources;
    serviceInstance <- servicePlan.serviceInstances
  ) {
    Console.println(s"Service ${service} has plan ${servicePlan} with instance ${serviceInstance}")
  }

  val servicePlan: Resource = client.servicePlans(first = true)
  val space: Resource = client.spaces(first = true)
  val serviceInstance: Resource = servicePlan.serviceInstance
  serviceInstance.name = "foobar"
  serviceInstance.space = space
  serviceInstance.servicePlan = servicePlan
  serviceInstance.save
  
  serviceInstance.name = "foobaz"
  serviceInstance.save
  
  serviceInstance.destroy

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