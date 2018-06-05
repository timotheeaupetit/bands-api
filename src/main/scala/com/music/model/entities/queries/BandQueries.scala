package com.music.model.entities.queries

import com.music.model.entities.Band

class BandQueries extends NodeQueries[Band] {

  override def findAll(): List[Band] = ???

  override def find(id: String): Band = ???

  override def delete(id: String): Unit = ???

  override def create(t: Band): Unit = ???

  override def update(t: Band): Unit = ???
}
