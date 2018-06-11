package com.music.api.model.entities.queries

import com.music.api.model.entities.types.Album
import org.neo4j.driver.v1.{Record, Value}

class AlbumQueries extends NodeQueries[Album] {

  final override def findAll(): List[Album] = ???

  final override def findById(id: String): Option[Album] = ???

  final override def findByName(name: String): Option[Album] = ???

  final override def delete(id: String): Option[Album] = ???

  final override def save(album: Album): Album = ???

  final override protected def buildFrom(record: Record): Album = ???

  final override protected def setParams(before: Option[Album], after: Album): Value = ???
}
