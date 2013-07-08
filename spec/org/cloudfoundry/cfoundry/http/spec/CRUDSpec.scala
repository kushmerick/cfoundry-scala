package org.cloudfoundry.cfoundry.http.spec

import org.scalatest._
import org.scalatest.matchers._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.exceptions._

class CRUDSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  
  import CRUD._
  
  class MinimalCRUD(_endpoint: String) extends CRUD(_endpoint, null) {
    override def Crud(path: Path, headers: Option[Pairs], payload: Option[String]) = null
    override def cRud(path: Path, headers: Option[Pairs]) = null
    override def crUd(path: Path, headers: Option[Pairs], payload: Option[String]) = null
    override def cruD(path: Path, headers: Option[Pairs]) = null
  }
  
  val endpoint = "http://foobar"

  "CRUD" should "require an endpoint" in {
    intercept[NoEndpoint] {
      new MinimalCRUD(null)
    } 
  } 
    
  it should "drop trailing /" in {
    var crud = new MinimalCRUD(s"${endpoint}/")
    crud.endpoint should equal(endpoint)
    crud = new MinimalCRUD(endpoint)
    crud.endpoint should equal(endpoint)
  }
  
  it should "chain path elements correctly" in {
    val path = Seq(Left("a"), Left("b"), Right(Seq("c", "d")))
    val crud = new MinimalCRUD(endpoint)
    crud.makePath(path) should equal("a/b/c/d")
        
  }

}