# cfoundry-scala

cfoundry-scala is a 'native' Scala reimplementation of the "cfoundry" CF Ruby client (http://github.com/cloudfoundry/cfoundry).

## Using cfoundry-scala from Scala

https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/scala

    val client: Client = new Client(target)
    client.login(username, password)
    for (service <- client.services) {
      for (servicePlan <- service.servicePlans) {
        ...
      }
    }
    client.logout

## Using cfoundry-scala from Java

https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/java

    Client client = new Client(target);
    client.login(username, password);
    for (Service service : client.getServices()) {
	  for (ServicePlan servicePlan : service.getServicePlans()) {
        ...
      }
    }
    client.logout();
 