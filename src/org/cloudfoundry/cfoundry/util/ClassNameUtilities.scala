package org.cloudfoundry.cfoundry.util

import scala.collection.mutable._

trait ClassNameUtilities {

  def getBriefClassName: String = getBriefClassName(getClass)

  def getBriefClassName(c: Class[_]) = {
    c.getName.substring(c.getPackage.getName.length + 1)
  }

  def getClass(siblingClass: Class[_], className: String) = {
    Class.forName(siblingClass.getPackage.getName + '.' + className)
  }

}
