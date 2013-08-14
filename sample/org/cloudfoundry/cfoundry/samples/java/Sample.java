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

		System.out.println("CF version: " + client.getCloudfoundryVersion() + "; client version: " + client.getVersion());

		client.login(username, password);

		for (Organization org : client.getOrganizations()) {
			for (Space space : org.getSpaces()) {
				System.out.println("Org " + org + " has space " + space);
			}
		}
		
		for (Service service : client.getServices()) {
			for (ServicePlan servicePlan : service.getServicePlans()) {
				for (ServiceInstance serviceInstance : servicePlan.getServiceInstances()) {
					System.out.println("Service " + service + " has plan " + servicePlan
							+ " with instance " + serviceInstance);
				}
			}
		}

		ServicePlan servicePlan = client.getServices().get(0).getServicePlans().get(0);
		Space space = client.getSpaces().get(0);
		ServiceInstance serviceInstance = servicePlan.newServiceInstance();
		serviceInstance.setName("foobar");
		serviceInstance.setSpace(space);
		serviceInstance.setServicePlan(servicePlan);
		serviceInstance.save();

		serviceInstance.setName("foobar");
		serviceInstance.save();
		
		serviceInstance.destroy();

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