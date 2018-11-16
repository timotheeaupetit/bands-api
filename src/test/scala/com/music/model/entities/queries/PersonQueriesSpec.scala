package com.music.model.entities.queries

import akka.http.scaladsl.testkit.Specs2RouteTest
import org.specs2.Specification
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

class PersonQueriesSpec extends Specification with Specs2RouteTest {

  def is: SpecStructure =
    s2"""
        | Sample $findExisting
      """.stripMargin

  private def findExisting: MatchResult[Boolean] = true must beEqualTo(true)

}
