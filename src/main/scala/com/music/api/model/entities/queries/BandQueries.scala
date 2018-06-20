package com.music.api.model.entities.queries

import java.util.UUID

import com.music.api.model.entities.types.Band
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._
import scala.util.Try

class BandQueries(implicit session: Session) extends NodeQueries[Band] {

  final override def findAll(): List[Band] = {
    val FIND_ALL =
      """MATCH (b:Band)
        |RETURN b
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result
      .list[Band](buildFrom)
      .asScala
      .toList
  }

  final override def findById(uuid: String): Option[Band] = {
    val FIND =
      """MATCH (b:Band {uuid: $uuid})
        |RETURN b
      """.stripMargin

    val result = session.run(FIND, parameters("uuid", uuid))

    Try(buildFrom(result.single())).toOption
  }

  final override def findByName(name: String): Option[Band] = {
    val FIND =
      """MATCH (b:Band {name: $name})
        |RETURN b
      """.stripMargin

    val result = session.run(FIND, parameters("name", name))

    Try(buildFrom(result.single())).toOption
  }

  final override def delete(uuid: String): Option[Band] = {
    val DELETE =
      """MATCH (b:Band {uuid: $uuid})
        |DETACH DELETE b
      """.stripMargin

    val result = session.run(DELETE, parameters("uuid", uuid))

    Try(buildFrom(result.single())).toOption
  }

  final override def save(band: Band): Band = {
    val maybeBand = band.uuid.flatMap(findById)
    val MERGE =
      """MERGE (b:Band {uuid: $uuid, name: $name})
        |ON CREATE SET b.formed = $formed, b.disbanded = $disbanded, b.aka = $aka, b.country = $country
        |ON MATCH SET b.formed = $formed, b.disbanded = $disbanded, b.aka = $aka, b.country = $country
        |RETURN b
      """.stripMargin

    val params = setParams(maybeBand, band)

    val result = session.run(MERGE, params)

    buildFrom(result.single())
  }

  final override protected def buildFrom(record: Record): Band = {
    val node = record.get(0)
    Band(
      uuid = Some(node.get("uuid").asString()),
      name = node.get("name").asString(),
      aka = Some(node.get("aka").asString()),
      country = Some(node.get("country").asString()),
      formed = Some(node.get("formed").asString()),
      disbanded = Some(node.get("disbanded").asString())
    )
  }

  final override protected def setParams(before: Option[Band], after: Band): Value = before match {
    case Some(band) =>
      parameters(
        "uuid",
        band.uuid,
        "name",
        band.name,
        "formed",
        after.formed.getOrElse(band.formed.getOrElse("null")),
        "disbanded",
        after.disbanded.getOrElse(band.disbanded.getOrElse("null")),
        "aka",
        after.aka.getOrElse(band.aka.getOrElse("null")),
        "country",
        after.country.getOrElse(band.country.getOrElse("null"))
      )
    case None =>
      val uuid = UUID.randomUUID().toString
      parameters(
        "uuid",
        uuid,
        "name",
        after.name,
        "formed",
        after.formed.getOrElse("null"),
        "disbanded",
        after.disbanded.getOrElse("null"),
        "aka",
        after.aka.getOrElse("null"),
        "country",
        after.country.getOrElse("null")
      )
  }
}
