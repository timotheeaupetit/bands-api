package com.music.api.model.entities.queries

import com.music.api.model.entities.types.Band
import org.neo4j.driver.v1.Record

class BandQueries extends NodeQueries[Band] {

  final override def findAll(): List[Band] = ???

  final override def find(id: String): Band = ???

  final override def delete(id: String): Unit = ???

  final override def create(band: Band): Band = ???

  final override def update(band: Band): Band = ???

  final override protected def buildFrom(record: Record): Band = ???
}
