package com.music.model.entities.queries

import com.music.model.entities.Album

class AlbumQueries extends NodeQueries[Album] {

  override def findAll(): List[Album] = ???

  override def find(id: String): Album = ???

  override def delete(id: String): Unit = ???

  override def create(t: Album): Unit = ???

  override def update(t: Album): Unit = ???
}