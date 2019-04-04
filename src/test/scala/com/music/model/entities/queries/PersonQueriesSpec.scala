package com.music.model.entities.queries

import java.util.UUID

import com.music.model.entities.types.Person
import org.specs2.Specification
import org.specs2.matcher.MatchResult
import org.specs2.mock.Mockito
import org.specs2.specification.core.SpecStructure

class PersonQueriesSpec extends Specification with Mockito {

  def is: SpecStructure =
    s2"""
        PersonQueries should act like this:
        | Return a person ${c().findExisting}
      """.stripMargin

  case class c() {
    val m: PersonQueries = mock[PersonQueries]

    def findExisting: MatchResult[Any] = {
      val uuid = UUID.randomUUID()
      val person = new Person(uuid,
                              fullName = "John Smith",
                              firstName = Some("John"),
                              lastName = Some("Smith"),
                              aka = None,
                              born = None)

      m.findById(uuid) returns Some(person)
      m.findById(uuid).map(_.fullName) must beSome("John Smith")
    }
  }

}
