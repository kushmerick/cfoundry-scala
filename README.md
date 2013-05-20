# cfoundry-scala

cfoundry-scala is a 'native' Scala reimplementation of [cfoundry|http://github.com/cloudfoundry/cfoundry].

## Using cfoundry-scala [from Scala|src/org/cloudfoundry/cfoundry/samples/scala]

    val client: Client = new Client(target)
    client.login(username, password)
    for (service <- client.services) {
      ...
    }
    client.logout

## Using cfoundry-scala [from Java|src/org/cloudfoundry/cfoundry/samples/java]

    Client client = new Client(target);
    client.login(username, password);
    for (Resource service: (List<Resource>) client.oo("services")) {
      ...
    }
    client.logout();
