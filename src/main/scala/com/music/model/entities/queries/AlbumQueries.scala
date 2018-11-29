package com.music.model.entities.queries

import java.util.UUID

import com.music.model.entities.types.{Album, NewAlbum}
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class AlbumQueries(implicit session: Session) extends NodeQueries[NewAlbum, Album] {

  final override def create(newAlbum: NewAlbum): Option[Album] = {
    val album = Album(newAlbum)
    val result = session.run(AlbumQueries.CREATE, AlbumQueries.setParams(album))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    Try(session.run(AlbumQueries.DELETE, AlbumQueries.setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Album " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Album] = {
    val result = session.run(AlbumQueries.FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Album] = {
    Try(session.run(AlbumQueries.FIND_BY_ID, AlbumQueries.setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(title: String): Option[Album] = {

    val result = session.run(AlbumQueries.FIND_BY_NAME, parameters("title", title))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(album: Album): Option[Album] = {
    Try(session.run(AlbumQueries.UPDATE, AlbumQueries.setParams(album))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override protected def buildFrom(record: Record): Option[Album] = {
    for {
      node <- Option(record.get(0))
      _uuid <- Try(UUID.fromString(node.get("uuid").asString())).toOption
    } yield {
      Album(uuid = _uuid,
            title = node.get("title").asString(),
            releaseDate = Try(node.get("released").asString().toInt).toOption)
    }
  }
}

object AlbumQueries {
  val CREATE: String =
    """MERGE (a:Album {title: $title})
      |ON CREATE SET a.uuid = $uuid,
      |              a.released = $released
      |ON MATCH SET a.released = $released
      |RETURN a
    """.stripMargin

  val DELETE: String =
    """MATCH (a:Album {uuid: $uuid})
      |DETACH DELETE a
    """.stripMargin

  val FIND_ALL: String =
    """MATCH (a:Album)
      |RETURN a
      |LIMIT 50
    """.stripMargin

  val FIND_BY_ID: String =
    """MATCH (a:Album {uuid: $uuid})
      |RETURN a
    """.stripMargin

  val FIND_BY_NAME: String =
    """MATCH (a:Album {title: $title})
      |RETURN a
    """.stripMargin

  val UPDATE: String =
    """MERGE (a:Album {uuid: $uuid})
      |ON MATCH SET title = $title,
      |             a.released = $released
      |RETURN a
    """.stripMargin

  def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)

  def setParams(album: Album): Value = {
    parameters("uuid", album.uuid.toString,
               "title", album.title,
               "released", album.releaseDate.getOrElse("null").toString)
  }
}
