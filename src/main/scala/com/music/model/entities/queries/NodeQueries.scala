package com.music.model.entities.queries

import java.util.UUID

import org.neo4j.driver.Record

abstract class NodeQueries[N, T] {

  def create(n: N): Option[T]

  def delete(uuid: UUID): Option[Throwable]

  def findAll(): List[T]

  def findById(uuid: UUID): Option[T]

  def findByName(name: String): Option[T]

  def update(t: T): Option[T]

  protected def buildFrom(record: Record): Option[T]

}
