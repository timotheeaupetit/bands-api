package com.music.model

import com.music.model.entities.queries.{BandQueries, PersonQueries}
import com.music.model.entities.types.{Band, NewPerson, Person}
import com.music.model.relationships.queries.PlayedInQueries
import com.music.model.relationships.types.PlayedIn
import org.neo4j.driver.{Session, Result, Transaction}

class BandPageQueries(session: Session) {
  private def addBand(tx: Transaction, band: Band): Result =
    tx.run(BandQueries.CREATE, BandQueries.setParams(band))

  private def addPerson(tx: Transaction, person: Person): Result = {
    tx.run(PersonQueries.CREATE, PersonQueries.setParams(person))
  }

  private def linkMember(tx: Transaction, person: Person, playedIn: PlayedIn, band: Band): Result = {
    val params = PlayedInQueries.params(person.uuid, playedIn, band.uuid)
    tx.run(PlayedInQueries.PLAYED_IN, params)
  }

  def save(bandPage: BandPage): Unit = {
    val band = Band(bandPage)
    session.writeTransaction(tx => addBand(tx, band))

    bandPage.members.foreach { member =>
      val person = Person(NewPerson(member))

      session.writeTransaction(tx => addPerson(tx, person))

      member.periods.foreach { period =>
        val playedIn = PlayedIn(period = period, member.instruments)

        session.writeTransaction(tx => linkMember(tx, person, playedIn, band))
      }
    }
  }
}
