package com.music.repository

import java.util.UUID

import com.music.model.entities.types.{Band, NewBand}
import org.specs2.Specification
import org.specs2.matcher.{MatchResult, Matchers}
import org.specs2.mock.Mockito
import org.specs2.specification.core.SpecStructure

class BandRepositorySpec extends Specification with BandRepository with Matchers with Mockito {
  private val bands = collection.mutable.Map.empty[UUID, Band]

  def is: SpecStructure =
    s2"""
         A band should be created if it doesn't exist $createFromScratch
         A band should be updated if it exists $updateExisting
      """

  private def createFromScratch: MatchResult[Any] = {
    val newBand = NewBand(
      name = "MyNewBand",
      country = Some("France"),
      formed = None,
      disbanded = None
    )

    val band = this.create(newBand)

    band.map(_.name) must beSome(newBand.name)
  }

  private def updateExisting: MatchResult[Any] = {
    val bandToUpdate = bands.head._2
    val updatedBand = bandToUpdate.copy(
      formed = Some(2012)
    )

    val resultBand = this.update(updatedBand)

    resultBand.map(_.uuid) must beSome(bandToUpdate.uuid)
  }

  override def create(newBand: NewBand): Option[Band] = {
    val band: Band = Band(newBand)
    bands.update(band.uuid, band)

    Some(band)
  }

  override def update(band: Band): Option[Band] = ???

  override def delete(uuid: UUID): Option[Throwable] = ???

  override def findAll(): Seq[Band] = ???

  override def findById(uuid: UUID): Option[Band] = ???
}
