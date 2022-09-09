package com.music.model.entities.queries

import com.music.model.entities.types.{Band, NewBand}
import org.neo4j.driver.Values.parameters
import org.neo4j.driver.{Record, Session, Value}

import java.util.UUID
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class BandQueries(implicit session: Session) extends NodeQueries[NewBand, Band] {

  final override def create(newBand: NewBand): Option[Band] = {
    val band = Band(newBand)
    val result = session.run(BandQueries.CREATE, BandQueries.setParams(band))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    Try(session.run(BandQueries.DELETE, BandQueries.setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Band " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Band] = {
    val result = session.run(BandQueries.FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Band] = {
    Try(session.run(BandQueries.FIND_BY_ID, BandQueries.setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(name: String): Option[Band] = {
    val result = session.run(BandQueries.FIND_BY_NAME, parameters("name", name))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(band: Band): Option[Band] = {
    Try(session.run(BandQueries.UPDATE, BandQueries.setParams(band))) match {
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
}

object BandQueries {
  val CREATE: String =
    """MERGE (b:Band {name: $name})
      |ON CREATE SET b.uuid = $uuid,
      |              b.formed = $formed,
      |              b.disbanded = $disbanded,
      |              b.country = $country
      |ON MATCH SET b.formed = $formed,
      |             b.disbanded = $disbanded,
      |             b.country = $country
      |RETURN b
    """.stripMargin

  val DELETE: String =
    """MATCH (b:Band {uuid: $uuid})
      |DETACH DELETE b
    """.stripMargin

  val FIND_ALL: String =
    """MATCH (b:Band)
      |RETURN b
      |LIMIT 50
    """.stripMargin

  val FIND_BY_ID: String =
    """MATCH (b:Band {uuid: $uuid})
      |RETURN b
    """.stripMargin

  val FIND_BY_NAME: String =
    """MATCH (b:Band {name: $name})
      |RETURN b
    """.stripMargin

  val UPDATE: String =
    """MERGE (b:Band {uuid: $uuid})
      |ON MATCH SET name = $name, b.formed = $formed, b.disbanded = $disbanded, b.country = $country
      |RETURN b
    """.stripMargin

  final def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)

  final def setParams(band: Band): Value = parameters("uuid", band.uuid.toString,
                                                      "name", band.name,
                                                      "formed", band.formed.getOrElse("null").toString,
                                                      "disbanded", band.disbanded.getOrElse("null").toString,
                                                      "country", band.country.getOrElse("null"))
}
