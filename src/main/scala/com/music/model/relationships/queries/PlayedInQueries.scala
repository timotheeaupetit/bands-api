package com.music.model.relationships.queries

import com.music.model.entities.types.{Band, Person}
import com.music.model.relationships.types.PlayedIn
import org.neo4j.driver.v1.Session
import org.neo4j.driver.v1.Values.parameters

class PlayedInQueries(implicit session: Session) {
  def link(person: Person, band: Band, playedIn: PlayedIn): Unit = {
    val LINK =
      """MATCH (b: Band {uuid: $band_id}), (p:Person {uuid: $person_id})
         MERGE (p)-[r:PLAYED_IN {instruments: $instruments, start: $start, end: $end}]->(b)
      """.stripMargin

    val instruments = playedIn.instruments.mkString(",")

    val params = parameters("band_id", band.uuid.toString,
                            "person_id", person.uuid.toString,
                            "instruments", instruments,
                            "start", playedIn.start.getOrElse("null").toString,
                            "end", playedIn.end.getOrElse("null").toString)

    session.run(LINK, params)
  }

}
