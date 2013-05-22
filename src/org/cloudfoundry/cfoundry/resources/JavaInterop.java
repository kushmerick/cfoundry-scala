package org.cloudfoundry.cfoundry.resources;

public class JavaInterop {

	public static Resource asResource(Magic magic) {
		return magic.resource();
	}

	public static java.lang.Iterable<Resource> asResources(Magic magic) {
		return scala.collection.JavaConversions.asJavaIterable(magic.resources());
	}

}
