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
  for (service <- client.services) {
    for (servicePlan <- service.servicePlans) {
      Console.println(s"Service ${service} has plan ${servicePlan}")
    }
  }

  /*
  val services: Iterable[Resource] = client.services
  val service = services.iterator.next
  val servicePlans = service.servicePlans
  val servicePlan = servicePlans.iterator.next
  val org: Resource = client.organization
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