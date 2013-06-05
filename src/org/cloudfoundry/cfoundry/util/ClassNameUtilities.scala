package org.cloudfoundry.cfoundry.util

import scala.collection.mutable._

trait ClassNameUtilities {

  def getBriefClassName: String = getBriefClassName(getClass)

  def getBriefClassName(c: Class[_]) = {
    c.getName.substring(c.getPackage.getName.length + 1)
  }

  def getSiblingClass(siblingClassName: String, ofClass: Class[_] = getClass): Class[_] = {
    Class.forName(ofClass.getPackage.getName + '.' + siblingClassName)
  }

}
