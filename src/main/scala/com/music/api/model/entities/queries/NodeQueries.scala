package com.music.api.model.entities.queries

abstract class NodeQueries[T] {
  def findAll(): List[T]
  def find(id: String): T
  def delete(id: String): Unit
  def create(t: T): Unit
  def update(t: T): Unit
}
