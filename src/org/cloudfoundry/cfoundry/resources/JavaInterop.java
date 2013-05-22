package org.cloudfoundry.cfoundry.resources;

import java.lang.Iterable;
import java.util.*;

/*
 * Ugg, is there a better way -- scala.collection.JavaConversions loses type parameters?!
 */

public class JavaInterop {

	public static Resource asResource(Magic magic) {
		return magic.resource();
	}

	public static Iterable<Resource> asResources(Magic magic) {
		scala.collection.Seq<Resource> resources = magic.resources();
		Resource[] result = new Resource[resources.size()];
		scala.collection.Iterator<Resource> i = resources.iterator();
		int j = 0;
		while (i.hasNext()) {
			result[j++] = i.next(); // TODO: Avoid copying
		}
		return Arrays.asList(result);
	}

}
