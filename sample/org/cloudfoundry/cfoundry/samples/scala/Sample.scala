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

  for (org <- client.organizations) {
    for (space <- org.spaces) {
      Console.println(s"Org ${org} has space ${space}")
    }
  }
  
  val id = client.organizations(0).id
  val org = client.organizations(id).resource
  Console.println(s"Found organization ${org} with id ${id}")

  for (
    service <- client.services;
    servicePlan <- service.servicePlans;
    serviceInstance <- servicePlan.serviceInstances
  ) {
    Console.println(s"Service ${service} has plan ${servicePlan} with instance ${serviceInstance}")
  }

  val service: Resource = client.services(0)
  val servicePlan: Resource = service.servicePlans(0)
  val space: Resource = client.spaces(0)
  val serviceInstance: Resource = servicePlan.serviceInstance
  serviceInstance.name = "foobar"
  serviceInstance.space = space
  serviceInstance.servicePlan = servicePlan
  serviceInstance.save
  serviceInstance.destroy
  
  space.name = "foobar"
  space.save

  /* TODO!
   * 
   val org = client.organizations(0)
  val user = client.user
  user.username = "joe@example.com"
  user.save
  org.members << 
*/
  
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