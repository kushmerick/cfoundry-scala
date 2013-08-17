package org.cloudfoundry.cfoundry.samples.scala

import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.config._
import java.util.logging._
import java.nio.file._

object Sample extends scala.App {

  val (target, username, password) = arguments
  
  val client = new Client(target, logger)

  client.login(username, password)
  
  Console.println(s"CF version: ${client.cloudfoundryVersion.int}; client version = ${client.version.string}")
  
  client.customHeaders = Pairs("X-vCHS-vDC-Id" -> "foo123")
  
  for (org <- client.organizations.resources) {
    for (space <- org.spaces.resources) {
      Console.println(s"Org ${org} has space ${space}")
    }
  }
  
  for (
    service <- client.services.resources;
    servicePlan <- service.servicePlans.resources;
    serviceInstance <- servicePlan.serviceInstances.resources
  ) {
    Console.println(s"Service ${service} has plan ${servicePlan} with instance ${serviceInstance}")
  }

  val servicePlan: ServicePlan = client.servicePlans(first = true)
  val space: Space = client.spaces(first = true)
  val serviceInstance: ServiceInstance = servicePlan.serviceInstance
  serviceInstance.name = "foobar"
  serviceInstance.space = space
  serviceInstance.servicePlan = servicePlan
  serviceInstance.save
  serviceInstance.name = "foobaz"
  serviceInstance.save
  serviceInstance.destroy

  val app: App = client.app
  app.name = "blah"
  app.space = space
  val zip = "app.zip"
  val bits = Files.readAllBytes(Paths.get(Config.cfFixtures, zip))
  app.bits = zip -> bits
  app.save
  app.destroy
  
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