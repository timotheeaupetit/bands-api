package com.music.api.model.entities.queries

import org.neo4j.driver.v1.Record

abstract class NodeQueries[T] {
  def findAll(): List[T]
  def find(id: String): T
  def delete(id: String): Unit
  def create(t: T): T
  def update(t: T): T
  protected def buildFrom(record: Record): T
}
