package com.music.model.entities.types

import java.util.UUID

case class Album(uuid: UUID, title: String, releaseDate: Option[Int])

object Album {
  def apply(newAlbum: NewAlbum): Album = new Album(uuid = UUID.randomUUID(),
                                                   title = newAlbum.title,
                                                   releaseDate = newAlbum.releaseDate)
}

case class NewAlbum(title: String, releaseDate: Option[Int])
