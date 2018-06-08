package com.music.api.model.entities.queries

import com.music.api.model.entities.types.Album
import org.neo4j.driver.v1.Record

class AlbumQueries extends NodeQueries[Album] {

  final override def findAll(): List[Album] = ???

  final override def find(id: String): Album = ???

  final override def delete(id: String): Unit = ???

  final override def create(album: Album): Album = ???

  final override def update(album: Album): Album = ???

  final override protected def buildFrom(record: Record): Album = ???
}
