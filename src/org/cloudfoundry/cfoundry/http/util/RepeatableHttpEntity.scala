package org.cloudfoundry.cfoundry.http.util

import org.apache.http._
import org.apache.http.entity._
import java.io._

class RepeatableHttpEntity(entity: HttpEntity, maxExcerpt: Int) extends HttpEntityWrapper(entity) {

  private val origIsRepeatable = entity.isRepeatable
  private val origContent = entity.getContent

  private val content =
    if (origIsRepeatable || origContent.markSupported) {
      origContent
    } else {
      new BufferedInputStream(origContent, maxExcerpt)
    }

  override def getContent = content

  override def isRepeatable = true

}