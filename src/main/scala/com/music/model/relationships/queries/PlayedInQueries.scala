package com.music.model.relationships.queries

import java.util.UUID

import com.music.model.entities.types.{Band, Person}
import com.music.model.relationships.types.PlayedIn
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Session, Value}

class PlayedInQueries(implicit session: Session) {
  def link(person: Person, playedIn: PlayedIn, band: Band): Unit = {
    val params = PlayedInQueries.params(person.uuid, playedIn, band.uuid)

    session.run(PlayedInQueries.PLAYED_IN, params)
  }
}

object PlayedInQueries {
  val PLAYED_IN: String =
    """MATCH (b: Band {uuid: $band_id}), (p:Person {uuid: $person_id})
      |MERGE (p)-[:PLAYED_IN {instruments: $instruments, start: $start, end: $end}]->(b)
    """.stripMargin

  def params(personId: UUID, playedIn: PlayedIn, bandId: UUID): Value = {
    val instruments = playedIn.instruments.mkString(",")
    parameters("band_id", bandId.toString,
               "person_id", personId.toString,
               "instruments", instruments,
               "start", playedIn.start.getOrElse("null").toString,
               "end", playedIn.end.getOrElse("null").toString)
  }
}
