package com.music.connector

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.Specs2RouteTest
import com.music.Builder
import org.specs2.mutable.Specification

class ConnectorSpec extends Specification with Specs2RouteTest {

  private val connector: Connector = new Connector(Builder.projectConfigurationBuilder)

  private val routes = connector.routes

  "The API" should {
    "return a band object based on its name" in {
      Get("/bands?name=Tool") ~> routes ~> check {
        responseAs[String] shouldEqual """{"name": "Tool", "formed": 1990}"""
      }
    }

    "MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> routes ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
      }
    }
  }
}
