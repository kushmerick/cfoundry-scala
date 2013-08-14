package org.cloudfoundry.cfoundry.suite

import org.cloudfoundry.cfoundry.auth.spec._
import org.cloudfoundry.cfoundry.client.spec._
import org.cloudfoundry.cfoundry.http.spec._
import org.cloudfoundry.cfoundry.resources.spec._
import org.cloudfoundry.cfoundry.util.spec._
import java.util.logging._

class Suite
  extends org.scalatest.Suites(
    // auth
    new LoginClientSpec,
    new TokenSpec,
    // client
    new ClientSpec,
    // http
    new CRUDSpec,
    new EntitySpec,
    new ResponseSpec,
    // resources
    new AppSpec,
    new CacheSpec,
    new OrganizationSpec,
    new ResourceSpec,
    new ServiceInstanceSpec,
    new ServicePlanSpec,
    new ServiceSpec,
    new SpaceSpec,
    // util
    new ChaliceSpec,
    new ClassNameUtilitiesSpec,
    new InflectorSpec,
    new JSONSpec,
    new PairsSpec,
    new SanitizerSpec
  )
  with org.scalatest.BeforeAndAfterAll {
  
  override def beforeAll(configMap: Map[String, Any]) = {
    configureLogger
    starting(true)
  }
  
  override def afterAll(configMap: Map[String, Any]) = {
    starting(false)
  }
  
  var LOGFILE = "cfoundry-scala-spec.log"

  private def configureLogger = {
	val logger = Logger.getGlobal
    if (!logger.getHandlers.exists(handler => handler.isInstanceOf[ConsoleHandler])) {
      // TODO: The Eclipse console displays two copies of every message.  But if I don't
      // add this handler, then none are displayed?!
      logger.addHandler(new ConsoleHandler)
    }
    val logfile = new FileHandler(LOGFILE, 10<<20, 1, true) // keep last 10MB of logs
    logfile.setFormatter(new SimpleFormatter)
    logger.addHandler(logfile)
    logger.setLevel(Level.FINEST)
    logger.getHandlers.foreach(handler => handler.setLevel(Level.FINEST))
  }
  
  private def starting(start: Boolean) = {
    val s = if (start) "START" else "END"
    val t = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date)
    Logger.getGlobal.info(s"~ ~ ~ ~ ~ ${s}ING test suite ${this.getClass.getName} at ${t} ~ ~ ~ ~ ~")
  }

}