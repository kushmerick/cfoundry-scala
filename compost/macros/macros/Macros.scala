package org.cloudfoundry.cfoundry.macros

import scala.language.experimental.macros
import scala.reflect.macros.Context

object Macros {

  object PropertyDeclaration {

    def impl
     (c: Context)
     (resource: c.Expr[PropertyDeclarationProcessor],
      name: c.Expr[String],
      typ: c.Expr[String],
      source: c.Expr[String],
      default: c.Expr[Option[Any]],
      applicable: c.Expr[Boolean],
      readOnly: c.Expr[Boolean],
      parental: c.Expr[Boolean]):
     c.Expr[Unit] = {
      import c.universe._
      reify {
        resource.splice.processPropertyDeclaration(
          name.splice,
          typ.splice,
          source.splice,
          default.splice,
          applicable.splice,
          readOnly.splice,
          parental.splice)
      }
    }
    
   def declareProperty(
      resource: PropertyDeclarationProcessor,
      name: String,
      typ: String,
      source: String,
      default: Option[Any],
      applicable: Boolean,
      readOnly: Boolean,
      parental: Boolean) = macro impl

  }

}
