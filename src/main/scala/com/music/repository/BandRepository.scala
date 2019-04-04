package com.music.repository

import java.util.UUID

import com.music.model.entities.types.{Band, NewBand}

trait BandRepository extends NodeRepository[NewBand, Band] {

  def create(n: NewBand): Option[Band]

  def delete(uuid: UUID): Option[Throwable]

  def findAll(): Seq[Band]

  def findById(uuid: UUID): Option[Band]

  def update(t: Band): Option[Band]
}
