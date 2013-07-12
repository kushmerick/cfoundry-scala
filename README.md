# cfoundry-scala

cfoundry-scala is a Scala client for [Cloud
Foundry](http://cloudfoundry.com).

cfoundry-scala is a [work-in-progress](https://github.com/kushmerick/cfoundry-scala/blob/master/TODO.txt)!
The client is basically
stable/functional with no known bugs.  But it does not yet support
several CF resources (e.g. users, routes, domains, quotas, stacks),
and support for other resources is incomplete (e.g., you can't upload
an app's bits).

## Installation

Download the latest "[cfoundry-scala-*.jar](https://github.com/kushmerick/cfoundry-scala/tree/master/releases)"
and add it to your classpath.

cfoundry-scala depends on a bunch of other JARs.  For convenience,
"cfoundry-scala-*.jar" and all its dependencies are available as
"cfoundry-scala-*-complete.zip".

## Using cfoundry-scala from Scala

In Scala, the syntax for retrieving and manipulating CF resources
resembles navigating through a complex local datastructure.

    val client = new Client(target)
    client.login(username, password)
    for (service <- client.services) {
      for (servicePlan <- service.servicePlans) {
        ...
      }
    }
    client.logout
    
The full [Scala
sample](https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/scala)
shows many more details -- such as creating new resources, and writing local changes to a resource back to CF.

## Using cfoundry-scala from Java

cfoundry-scala also exposes a statically-typed [Java-friendly (getFoo,
setFoo, newFoo)
API](https://github.com/kushmerick/cfoundry-scala/tree/master/java_friendly_signatures/src/org/cloudfoundry/cfoundry):

    Client client = new Client(target);
    client.login(username, password);
    for (Service service : client.getServices()) {
	  for (ServicePlan servicePlan : service.getServicePlans()) {
        ...
      }
    }
    client.logout();
 
For more details, see the full [Java
sample](https://github.com/kushmerick/cfoundry-scala/tree/master/sample/org/cloudfoundry/cfoundry/samples/java).

(Of course, Scala code can also consume the statically-typed API!)

## Implementation details

Like the [cfoundry Ruby
client](http://github.com/cloudfoundry/cfoundry), cfoundry-scala
relies on metaprogramming so that each CF resource is specified in a
simple declarative manner.  For example, here is the
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

The [Java-friendly
API](https://github.com/kushmerick/cfoundry-scala/tree/master/java_friendly_signatures/src/org/cloudfoundry/cfoundry)
is [automatically
generated](https://github.com/kushmerick/cfoundry-scala/tree/master/tools/java-friendly-generator)
from these resource specifications.  I will
[reimplement](https://github.com/kushmerick/cfoundry-scala/blob/master/compost/macros/macros/Macros.scala)
this API using [Scala macros](http://scalamacros.org) when support for
adding new class members is ready for prime time.
