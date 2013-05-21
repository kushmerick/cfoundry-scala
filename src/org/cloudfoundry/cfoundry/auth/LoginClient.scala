package org.cloudfoundry.cfoundry.auth

import org.cloudfoundry.cfoundry.http._
import org.cloudfoundry.cfoundry.util._
import org.cloudfoundry.cfoundry.exceptions._
import java.util.logging._

class LoginClient[TCRUD <: CRUD](crudFactory: (String, Logger) => TCRUD, endpoint: String, logger: Logger) {

  import LoginClient._

  private val crud = crudFactory(endpoint, logger)

  implicit def s2essseq(s: String) = Left(s)
  implicit def sseq2essseq(sseq: Seq[String]) = Right(sseq)

  def login(username: String, password: String) = {
    val content = Pairs(
      "grant_type" -> "password",
      "username" -> username,
      "password" -> password)
    val payload = Some(new Payload(content.formEncode))
    val response = crud.create("oauth/token")(LOGIN_OPTIONS)(payload)
    if (response.ok) {
      new Token(response.payload)
    } else {
      throw new NotAuthorized(response, username)
    }
  }

}

object LoginClient {

  val LOGIN_OPTIONS = Some(Pairs(
    "Content-Type" -> CRUD.FORM_ENCODED,
    "Authorization" -> "Basic Y2Y6")) // TODO: What?!!?

}

/*

Authenticating--->
request: post http://login.cf-vchs.com/oauth/token
headers: {"content-type"=>"application/x-www-form-urlencoded;charset=utf-8", "accept"=>"application/json;charset=utf-8", "authorization"=>"Basic Y2Y6"}
body: grant_type=password&username=admin%40tempest.example.com&password=T3%40p0%2B                                                                           <---
response: 200
headers: {"cache-control"=>"no-cache, no-store, no-store", "content-length"=>"1858", "content-type"=>"application/json;charset=UTF-8", "date"=>"Sat, 11 May 2013 20:05:28 GMT", "expires"=>"Thu, 01 Jan 1970 00:00:00 GMT", "pragma"=>"no-cache, no-cache", "server"=>"Apache-Coyote/1.1"}
body: {"access_token":"eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIzMDAyYTIwNS0zMDE4LTQ3MzgtOTU4YS1kZjY1MDUyM2FkNTkiLCJzdWIiOiJjOWY4OWQxZS1kYTE0LTQyOGMtOWNjNS04NzU4NDMzYjZhMTEiLCJzY29wZSI6WyJjbG91ZF9jb250cm9sbGVyLmFkbWluIiwiY2xvdWRfY29udHJvbGxlci5yZWFkIiwiY2xvdWRfY29udHJvbGxlci53cml0ZSIsIm9wZW5pZCIsInBhc3N3b3JkLndyaXRlIiwic2NpbS5yZWFkIiwic2NpbS53cml0ZSJdLCJjbGllbnRfaWQiOiJjZiIsImNpZCI6ImNmIiwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwidXNlcl9pZCI6ImM5Zjg5ZDFlLWRhMTQtNDI4Yy05Y2M1LTg3NTg0MzNiNmExMSIsInVzZXJfbmFtZSI6ImFkbWluQHRlbXBlc3QuZXhhbXBsZS5jb20iLCJlbWFpbCI6ImFkbWluQHRlbXBlc3QuZXhhbXBsZS5jb20iLCJpYXQiOjEzNjgzMDI3NDMsImV4cCI6MTM2ODM0NTk0MywiaXNzIjoiaHR0cHM6Ly91YWEuY2YtdmNocy5jb20vb2F1dGgvdG9rZW4iLCJhdWQiOlsic2NpbSIsIm9wZW5pZCIsImNsb3VkX2NvbnRyb2xsZXIiLCJwYXNzd29yZCJdfQ.krx8Y_zHeQ6mj9LOMUB27MpwEZCg-hZvgYJkOkf0BYSHCCfmyEPoYuZpULdNHBqHkQpZ4ydREY4usK_sWGgKug","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJhNjYzZjVhOS1mZTQ2LTQ5ZDgtYmMzMS0zZTQzZmFlMDI4ZjMiLCJzdWIiOiJjOWY4OWQxZS1kYTE0LTQyOGMtOWNjNS04NzU4NDMzYjZhMTEiLCJzY29wZSI6WyJjbG91ZF9jb250cm9sbGVyLmFkbWluIiwiY2xvdWRfY29udHJvbGxlci5yZWFkIiwiY2xvdWRfY29udHJvbGxlci53cml0ZSIsIm9wZW5pZCIsInBhc3N3b3JkLndyaXRlIiwic2NpbS5yZWFkIiwic2NpbS53cml0ZSJdLCJpYXQiOjEzNjgzMDI3NDMsImV4cCI6MTM3MDg5NDc0MywiY2lkIjoiY2YiLCJpc3MiOiJodHRwczovL3VhYS5jZi12Y2hzLmNvbS9vYXV0aC90b2tlbiIsImdyYW50X3R5cGUiOiJwYXNzd29yZCIsInVzZXJfbmFtZSI6ImFkbWluQHRlbXBlc3QuZXhhbXBsZS5jb20iLCJhdWQiOlsiY2xvdWRfY29udHJvbGxlci5hZG1pbiIsImNsb3VkX2NvbnRyb2xsZXIucmVhZCIsImNsb3VkX2NvbnRyb2xsZXIud3JpdGUiLCJvcGVuaWQiLCJwYXNzd29yZC53cml0ZSIsInNjaW0ucmVhZCIsInNjaW0ud3JpdGUiXX0.tGmncGGLufuhwpFV0Eay_g4mQkXJ4UitPnYCAZmr5VjnXvqUtlNVSMwMsjRlBwWmmSMgx-TDC-bZ_VmPag_38g","expires_in":43199,"scope":"cloud_controller.admin cloud_controller.read cloud_controller.write openid password.write scim.read scim.write","jti":"3002a205-3018-4738-958a-df650523ad59"}

*/ 