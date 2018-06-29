package com.music.api.model.relationships.queries

import com.music.api.model.entities.types.{Band, Person}
import com.music.api.model.relationships.types.PlayedIn
import org.neo4j.driver.v1.Session
import org.neo4j.driver.v1.Values.parameters

class PlayedInQueries(implicit session: Session) {
  def link(person: Person, band: Band, playedIn: PlayedIn): Unit = {
    val LINK =
      """MATCH (b: Band {uuid: $band_id}), (p:Person {uuid: $person_id})
         MERGE (p)-[r:PLAYED_IN {instruments: $instruments, start: $start, end: $end}]->(b)
      """.stripMargin

    val params = parameters(
      "band_id",
      band.uuid.getOrElse("null"),
      "person_id",
      person.uuid.getOrElse("null"),
      "instruments",
      playedIn.instruments.getOrElse("null"),
      "start",
      playedIn.start.getOrElse("null"),
      "end",
      playedIn.end.getOrElse("null")
    )

    session.run(LINK, params)
  }

}
