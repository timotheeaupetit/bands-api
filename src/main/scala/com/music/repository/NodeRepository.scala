package com.music.repository

import java.util.UUID

trait NodeRepository[N, T] {
  def create(n: N): Option[T]

  def delete(uuid: UUID): Option[Throwable]

  def findAll(): Seq[T]

  def findById(uuid: UUID): Option[T]

  def update(t: T): Option[T]
}
