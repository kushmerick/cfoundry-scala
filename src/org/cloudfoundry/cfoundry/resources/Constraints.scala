package org.cloudfoundry.cfoundry.resources

import org.cloudfoundry.cfoundry.util._

class Constraint(val prop: String, val value: Any) extends Pair(prop, value) {

  private lazy val ids = Set("id", "guid")
  lazy val unique = ids.contains(prop)
  lazy val id = value match {
    case c: Chalice => c.string
    case a: Any => a.toString
  }

  private lazy val specials = Map(
    "depth" -> "inline-relations-depth",
    "inline-relation-depth" -> "inline-relations-depth",
    "recursive" -> "recursive",
    "page" -> "page",
    "results-per-page" -> "results-per-page")
  lazy val special = specials.contains(prop)
  lazy val specialProp = specials(prop)

  lazy val attribute = !unique && !special

}

class Constraints(_constraints: scala.Seq[Pair[String,Any]]) {
  
  val constraints = _constraints.map((kv) => new Constraint(kv._1, kv._2))

  lazy val nonEmpty = constraints.nonEmpty

  def exists(f: Constraint => Boolean) = constraints.exists(f)
  def filter(f: Constraint => Boolean) = constraints.filter(f)

  lazy val encode = {
    // 1. special constraints such as 'results-per-page=2'
    val specials = constraints.
      filter(_.special).
      map((constraint) => s"${constraint.specialProp}=${constraint.value}")
    // 2. attribute constraints such as 'q=foo:bar;baz=biz'
    val others = constraints.
      filter(_.attribute).
      map((constraint) => {
        // the next lines allow "users(id = token.user_id)", "services(name = service.name)", etc, as a
        // shorthand for "users(id = tokens.user_id.string)", "services(name = service.name.string)", etc
        val value = constraint.value match {
          case c: Chalice => c.string
          case m: MagicProp => m.string
          case a: Any => a.toString
        }
        s"${constraint.prop}:${value}" // TODO: Support more than ":" (https://github.com/cloudfoundry/cloud_controller_ng/blob/master/lib/vcap/rest_api/query.rb#L81)
      }).
      mkString(";")
    val uthers = if (others.isEmpty) Iterable.empty else Iterable(s"q=${others}")
    // 3. combine 1 + 2 
    var encoded = (specials ++ uthers).mkString("?", "&", "")
    // 4. handle /v2/foos/:id
    if (constraints.exists(_.unique)) {
      encoded = s"/${constraints.find(_.unique).get.id}${encoded}"
    }
    // all done
    encoded
  }

}

object Constraints {

  lazy val NONE = new Constraints(scala.Seq())

}
