package org.cloudfoundry.cfoundry.resources

import scala.collection.mutable._

/*
 * an LRU cache of Resources
 */

class Cache(capacity: Int) {

  def touch(resource: Resource): Unit = {
    if (contains(resource)) eject(resource)
    insert(new TimestampedResource(resource, tick))
  }

  def eject(resource: Resource): Unit = {
    history.get(resource._getId) match {
      case Some(timestampedResource) => remove(timestampedResource)
      case None =>
    }
  }

  def contains(resource: Resource): Boolean = {
    contains(resource._getId)
  }

  def contains(id: String) = {
    history.contains(id)
  }

  def get(id: String) = {
    history(id).resource
  }

  def getResources: Seq[Resource] = {
    // make a copy so that clients can safely touch/eject/etc
    // while iterating over all cached resources
    val resources = new Array[Resource](history.size)
    var i = 0
    for (timestampedResource <- history.values) {
      resources(i) = timestampedResource.resource
      i += 1
    }
    resources
  }

  ////

  private var timestamp = 0

  private def tick = {
    timestamp += 1
    timestamp
  }

  private class TimestampedResource(val resource: Resource, val timestamp: Int) extends Ordered[TimestampedResource] {
    override def compare(that: TimestampedResource) = timestamp - that.timestamp
  }

  private val cache = new TreeSet[TimestampedResource] // fast identification of the LRU resource
  private val history = new HashMap[String, TimestampedResource] // fast lookup by id

  private def insert(timestampedResource: TimestampedResource) = {
    ensureCapacity
    cache += timestampedResource
    history += timestampedResource.resource._getId -> timestampedResource
  }

  private def remove(timestampedResource: TimestampedResource) = {
    cache -= timestampedResource
    history -= timestampedResource.resource._getId
  }

  private def ensureCapacity = {
    while (cache.size >= capacity) {
      remove(eldest)
    }
  }

  private def eldest = {
    cache.iterator.next
  }

}