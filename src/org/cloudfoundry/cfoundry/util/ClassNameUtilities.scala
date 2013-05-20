package org.cloudfoundry.cfoundry.util

trait ClassNameUtilities {
  
  def getBriefClassName: String = getBriefClassName(getClass)
  
  def getBriefClassName(c: Class[_]) = {
    c.getName.substring(c.getPackage.getName.length+1)
  }
  
  def removeCompanion$(x: String) = {
    if (x.last == '$')
      x.substring(0, x.length-1)
    else
      x
  }
  
  def getClassQualification: String = getClassQualification(getClass)
  
  def getClassQualification(c: Class[_]) = {
    c.getPackage.getName.replace('/', '.')
  }

}
