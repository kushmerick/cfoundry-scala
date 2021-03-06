package org.cloudfoundry.cfoundry.util

class Inflector {

  def isPlural(noun: String) = noun.last == S

  def isSingular(noun: String) = !isPlural(noun)

  def singularize(noun: String) = {
    if (isSingular(noun))
      noun
    else
      noun.substring(0, noun.length - 1)
  }

  def pluralize(noun: String) = {
    if (isPlural(noun))
      noun
    else
      noun + S
  }

  def capitalize(noun: String) = {
    noun.capitalize
  }

  def lowerize(noun: String) = {
    val chars = noun.toCharArray
    chars(0) = chars(0).toLower
    new String(chars)
  }

  def camelToUnderline(camel: String) = {
    val underline = new StringBuilder
    var first = true
    for (c <- camel) {
      underline +=
        (if (c.isUpper) {
          if (!first) underline += U
          c.toLower
        } else {
          c
        })
      first = false
    }
    underline.result
  }

  def underlineToCamel(underline: String) = {
    val camel = new StringBuilder
    var cap = false
    for (c <- underline) {
      if (c == U) {
        cap = true
      } else {
        camel += (if (cap) c.toUpper else c)
        cap = false
      }
    }
    camel.result
  }

  private val S = 's'
  private val U = '_'

}
