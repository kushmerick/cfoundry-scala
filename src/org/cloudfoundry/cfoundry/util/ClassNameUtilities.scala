package org.cloudfoundry.cfoundry.util

trait ClassNameUtilities {

  def getBriefClassName: String = getBriefClassName(getClass)

  def getBriefClassName(c: Class[_]) = {
    c.getName.substring(c.getPackage.getName.length + 1)
  }

 def getClass(sibling: Class[_], className: String) = {
    Class.forName(sibling.getPackage.getName + '.' + className)
  }

}
