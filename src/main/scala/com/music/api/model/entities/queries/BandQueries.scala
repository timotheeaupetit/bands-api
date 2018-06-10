package com.music.api.model.entities.queries

import com.music.api.model.entities.types.Band
import org.neo4j.driver.v1.{Record, Value}

class BandQueries extends NodeQueries[Band] {

  final override def findAll(): List[Band] = ???

  final override def findById(id: String): Option[Band] = ???

  final override def findByName(name: String): Option[Band] = ???

  final override def delete(id: String): Option[Band] = ???

  final override def save(band: Band): Band = ???

  final override protected def buildFrom(record: Record): Band = ???

  final override protected def setParams(before: Option[Band], after: Band): Value = ???
}
