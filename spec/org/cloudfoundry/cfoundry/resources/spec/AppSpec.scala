package org.cloudfoundry.cfoundry.resources.spec

import org.scalatest.matchers._
import org.scalatest.fixture._
import org.cloudfoundry.cfoundry.resources._
import org.cloudfoundry.cfoundry.client.mock._
import org.cloudfoundry.cfoundry.config._
import java.nio.file._
import java.util.zip._
import java.io._

class AppSpec extends FlatSpec with ShouldMatchers with MockedClientFixture with CRUDTests with EnumerationTests with ResourceFixture {

  override val login = true

  "App" should "be CRUDable" in { client =>
    give a "space" from client to { space =>
      testCRUD(client, "app", Map("space" -> space))
    }
  }

  it should "be able to use a query to find itself" in { client =>
    give an "app" from client to { app =>
      testEnumerationId(client, "app", app)
    }
  }

  it should "support 'depth'" in { client =>
    testEnumerationDepth(client, "app")
  }

  it should "be able to upload and download bits" in { client =>
    give a "space" from client to { space =>
      var app: App = null
      try {
        app = client.app
        app.name = "blah"
        app.space = space
        val zip = "app.zip"
        val zippath = Paths.get(Config.cfFixtures, "app", zip)
        val bits = Files.readAllBytes(zippath)
        app.bits = zip -> bits
        app.save
        app.bits.clear
        val bits2: Array[Byte] = app.bits
        // The exact bits may differ because CF returns a fresh zip archive.
        // So we crack open the zips to inspect the actual files -- ugghh.
        val (zs1, zs2) = (
          new ZipInputStream(new FileInputStream(zippath.toFile)),
          new ZipInputStream(new ByteArrayInputStream(bits2)))
        var done = false
        while (!done) {
          val (e1, e2) = (zs1.getNextEntry, zs2.getNextEntry)
          if (e1 == null) {
            e2 should equal(null)
            done = true
          } else {
            e1.getSize should equal(e2.getSize)
            e1.isDirectory should equal(e2.isDirectory())
            if (!e1.isDirectory) {
              e1.getName should equal(e2.getName)
              val N = e1.getSize.toInt
              val (bytes1, bytes2) = (Array.fill[Byte](N)(0), Array.fill[Byte](N)(0))
              zs1.read(bytes1, 0, N)
              zs2.read(bytes2, 0, N)
              bytes1 should equal(bytes2)
            }
          }
        }
      } finally {
        if (app != null) app.destroy
      }
    }

  }

}