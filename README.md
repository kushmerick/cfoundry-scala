# cfoundry-scala

cfoundry-scala is a Scala client for Cloud Foundry (http://cloudfoundry.com).

## Using cfoundry-scala from Scala

In Scala, the syntax for retrieving and CF resources are retrieved and manipulated with a
syntax that looks just like navigating through a complex local datastructure.

    val client: Client = new Client(target)
    client.login(username, password)
    for (service <- client.services) {
      for (servicePlan <- service.servicePlans) {
        ...
      }
    }
    client.logout
    
The full [Scala sample](https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/scala)
shows many more details, such as creating new objects.    

## Using cfoundry-scala from Java

cfoundry-scala also exposes a statically-typed Java-friendly (getFoo, setFoo, newFoo) API:   

    Client client = new Client(target);
    client.login(username, password);
    for (Service service : client.getServices()) {
	  for (ServicePlan servicePlan : service.getServicePlans()) {
        ...
      }
    }
    client.logout();
 
For more details, see the full [Java sample](https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/java).

 ## Implementation details

Like the cfoundry Ruby client (http://github.com/cloudfoundry/cfoundry), cfoundry-scala relies on
metaprogramming to so that each CF resource is specified in a simple declarative manner.  For example, here is
[the Service resource](https://github.com/kushmerick/cfoundry-scala/blob/master/src/org/cloudfoundry/cfoundry/resources/Service.scala): 

    class Service ... {
      property("name", source = "label")
      property("provider")
      property("version")
      property("active", typ = "bool")
      hasMany("servicePlan")
        ...
    }
