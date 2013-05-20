package org.cloudfoundry.cfoundry.resources

trait Filter extends ((Resource) => Boolean)