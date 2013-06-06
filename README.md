# cfoundry-scala

cfoundry-scala is a Scala client for [Cloud Foundry](http://cloudfoundry.com).

## Using cfoundry-scala from Scala

In Scala, the syntax for retrieving and manipulating CF resources resembles
navigating through a complex local datastructure.

    val client = new Client(target)
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

cfoundry-scala also exposes a statically-typed
[Java-friendly (getFoo, setFoo, newFoo) API](https://github.com/kushmerick/cfoundry-scala/tree/master/java_friendly_signatures/src/org/cloudfoundry/cfoundry):   

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

Like the [cfoundry Ruby client](http://github.com/cloudfoundry/cfoundry), cfoundry-scala relies on
metaprogramming so that each CF resource is specified in a simple declarative manner.  For example, here is the
[Service](https://github.com/kushmerick/cfoundry-scala/blob/master/src/org/cloudfoundry/cfoundry/resources/Service.scala)
resource: 

    class Service ... {
      property("name", source = "label")
      property("provider")
      property("version")
      property("active", typ = "bool")
      hasMany("servicePlan")
        ...
    }

The [Java-friendly API](https://github.com/kushmerick/cfoundry-scala/tree/master/java_friendly_signatures/src/org/cloudfoundry/cfoundry)
is [automatically generated](https://github.com/kushmerick/cfoundry-scala/blob/master/build.xml) from these resource specifications.
I will [reimplement](https://github.com/kushmerick/cfoundry-scala/blob/master/compost/macros/macros/Macros.scala)
 this API using [Scala macros](http://scalamacros.org) when support for adding new class members is ready for prime time.
