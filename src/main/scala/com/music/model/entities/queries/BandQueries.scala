package com.music.model.entities.queries

import java.util.UUID

import com.music.model.entities.types.{Band, NewBand}
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class BandQueries(implicit session: Session) extends NodeQueries[NewBand, Band] {

  final override def create(newBand: NewBand): Option[Band] = {
    val band = Band(newBand)
    val CREATE =
      """MERGE (b:Band {name: $name})
        |ON CREATE SET uuid = $uuid, b.formed = $formed, b.disbanded = $disbanded, b.country = $country
        |ON MATCH SET uuid = $uuid, b.formed = $formed, b.disbanded = $disbanded, b.country = $country
        |RETURN b
      """.stripMargin

    val params = setParams(band)

    val result = session.run(CREATE, params)

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    val DELETE =
      """MATCH (b:Band {uuid: $uuid})
        |DETACH DELETE b
      """.stripMargin

    Try(session.run(DELETE, setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Band " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Band] = {
    val FIND_ALL =
      """MATCH (b:Band)
        |RETURN b
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Band] = {
    val FIND =
      """MATCH (b:Band {uuid: $uuid})
        |RETURN b
      """.stripMargin

    Try(session.run(FIND, setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(name: String): Option[Band] = {
    val FIND =
      """MATCH (b:Band {name: $name})
        |RETURN b
      """.stripMargin

    val result = session.run(FIND, parameters("name", name))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(band: Band): Option[Band] = {
    val MERGE =
      """MERGE (b:Band {uuid: $uuid})
        |ON MATCH SET name = $name, b.formed = $formed, b.disbanded = $disbanded, b.country = $country
        |RETURN b
      """.stripMargin

    val params = setParams(band)

    Try(session.run(MERGE, params)) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override protected def buildFrom(record: Record): Option[Band] = {
    for {
      node <- Option(record.get(0))
      _uuid <- Try(UUID.fromString(node.get("uuid").asString())).toOption
    } yield {
      Band(uuid = _uuid,
           name = node.get("name").asString(),
           country = Some(node.get("country").asString()),
           formed = Try(node.get("formed").asString().toInt).toOption,
           disbanded = Try(node.get("disbanded").asString().toInt).toOption)
    }
  }

  final override def setParams(band: Band): Value = {
    parameters("uuid", band.uuid.toString,
               "name", band.name,
               "formed", band.formed.getOrElse("null").toString,
               "disbanded", band.disbanded.getOrElse("null").toString,
               "country", band.country.getOrElse("null"))
  }

  final override protected def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)
}
