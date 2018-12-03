package com.music.model.entities.types

import java.util.UUID

import com.music.model.BandPage

case class Band(uuid: UUID,
                name: String,
                country: Option[String],
                formed: Option[Int],
                disbanded: Option[Int])

object Band {
  def apply(uuid: UUID,
            name: String,
            country: Option[String],
            formed: Option[Int],
            disbanded: Option[Int]): Band = new Band(uuid, name, country, formed, disbanded)

  def apply(newBand: NewBand): Band = new Band(uuid = UUID.randomUUID(),
                                               name = newBand.name,
                                               country = newBand.country,
                                               formed = newBand.formed,
                                               disbanded = newBand.disbanded)

  def apply(bandPage: BandPage): Band = new Band(uuid = UUID.randomUUID(),
                                                 name = bandPage.name,
                                                 country = bandPage.formed.location.map(_.country),
                                                 formed = bandPage.formed.date,
                                                 disbanded = bandPage.disbanded.date)

}

case class NewBand(name: String,
                   country: Option[String],
                   formed: Option[Int],
                   disbanded: Option[Int])
