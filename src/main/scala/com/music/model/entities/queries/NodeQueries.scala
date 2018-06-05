package com.music.model.entities.queries

trait NodeQueries[T] {
  def findAll(): List[T]
  def find(id: String): T
  def delete(id: String): Unit
  def create(t: T): Unit
  def update(t: T): Unit
}
