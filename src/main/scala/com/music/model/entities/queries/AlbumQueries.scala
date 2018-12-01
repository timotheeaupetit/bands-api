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
    val CREATE =
      """MERGE (a:Album {title: $title})
        |ON CREATE SET a.uuid = $uuid, a.released = $released
        |ON MATCH SET a.uuid = $uuid, a.released = $released
        |RETURN a
      """.stripMargin

    val params = setParams(album)

    val result = session.run(CREATE, params)

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    val DELETE =
      """MATCH (a:Album {uuid: $uuid})
        |DETACH DELETE a
      """.stripMargin

    Try(session.run(DELETE, setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Album " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Album] = {
    val FIND_ALL =
      """MATCH (a:Album)
        |RETURN a
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Album] = {
    val FIND =
      """MATCH (a:Album {uuid: $uuid})
        |RETURN a
      """.stripMargin

    Try(session.run(FIND, setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(name: String): Option[Album] = {
    val FIND =
      """MATCH (a:Album {title: $title})
        |RETURN a
      """.stripMargin

    val result = session.run(FIND, parameters("title", name))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(album: Album): Option[Album] = {
    val MERGE =
      """MERGE (a:Album {uuid: $uuid})
        |ON MATCH SET title = $title, a.released = $released
        |RETURN a
      """.stripMargin

    val params = setParams(album)

    Try(session.run(MERGE, params)) match {
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

  final override protected def setParams(album: Album): Value = {
    parameters("uuid", album.uuid.toString,
               "title", album.title,
               "released", album.releaseDate.getOrElse("null").toString)
  }

  final override protected def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)
}
