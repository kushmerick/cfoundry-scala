package org.cloudfoundry.cfoundry.samples.scala

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import java.util.logging._

object Sample extends scala.App {

  val (target, username, password) = arguments
  val client: Client = new Client(target, logger)

  client.login(username, password)
  for (org <- client.organizations) {
    for (space <- org.spaces) {
      Console.println(s"Org ${org} has space ${space}")
    }
  }
  for (service <- client.services;
       servicePlan <- service.servicePlans;
       serviceInstance <- servicePlan.serviceInstances) {
    Console.println(s"Service ${service} has plan ${servicePlan} with instance ${serviceInstance}")
  }

  /* the following almost works ....
  val service = client.services.iterator.next
  val servicePlan = service.servicePlans.iterator.next
  val space = client.spaces.iterator.next
  val org = space.organization
  val serviceInstance = servicePlan.serviceInstance
  serviceInstance.name = "foobar"
  Console.println("Service instance: ${serviceInstance}")
  todo for 'parents': insert guid into 'entity' not full embedded object
  serviceInstance.servicePlan = servicePlan
  serviceInstance.space = space
  Console.println("Service instance: ${serviceInstance}")
  serviceInstance.refresh
  todo for save: payload generate true json
  // serviceInstance.save
  // serviceInstance.destroy
  */
  /*
  org.name = "foo org"
  org.save
  val space: Resource = client.space
  space.name = "foo space"
  space.organization = org
  space.save
  val serviceInstance: Resource = client.serviceInstance
  serviceInstance.servicePlan = servicePlan
  serviceInstance.space = space
  serviceInstance.save
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