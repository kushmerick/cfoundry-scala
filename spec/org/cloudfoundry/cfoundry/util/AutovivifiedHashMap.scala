package org.cloudfoundry.cfoundry.util

import scala.collection.mutable.HashMap

class AutovivifiedHashMap[K, V](factory: () => V) {

  val hashmap = new HashMap[K, Option[V]]

  def apply(k: K): V = {
    var v = hashmap(k)
    v match {
      case None =>
        v = Option(factory()); hashmap(k) = v
      case _ =>
    }
    v.get
  }

  def update(k: K, v: V) = {
    hashmap(k) = Option(v)
  }

}