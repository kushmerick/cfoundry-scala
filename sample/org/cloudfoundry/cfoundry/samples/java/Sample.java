package org.cloudfoundry.cfoundry.samples.java;

import org.cloudfoundry.cfoundry.client.*;
import org.cloudfoundry.cfoundry.resources.*;
import java.util.logging.*;

class Sample {

	public static void main(String[] args) {
		String target = args[0];
		String username = args[1];
		String password = args[2];
		Client client = new Client(target, logger());
		client.login(username, password);
		for (Resource org : client.o("organizations").asResources()) {
			for (Resource space : org.o("spaces").asResources()) {
				System.out.println("Org " + org + " has space " + space);
			}
		}
		for (Resource service : client.o("services").asResources()) {
			for (Resource servicePlan : service.o("servicePlans").asResources()) {
				System.out.println("Service " + service + " has plan "
						+ servicePlan);
			}
		}
		client.logout();
	}

	static private Logger logger() {
		ConsoleHandler handler = new ConsoleHandler();
		Logger logger = Logger.getGlobal();
		logger.addHandler(handler);
		Level level = Level.FINEST;
		handler.setLevel(level);
		logger.setLevel(level);
		return logger;
	}

}