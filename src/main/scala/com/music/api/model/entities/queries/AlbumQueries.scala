package com.music.api.model.entities.queries

import java.util.UUID

import com.music.api.model.entities.types.Album
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._

class AlbumQueries(implicit session: Session) extends NodeQueries[Album] {

  final override def findAll(): List[Album] = {
    val FIND_ALL =
      """MATCH (a:Album)
        |RETURN a
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result.asScala.toList
      .flatten(buildFrom)
  }

  final override def findById(uuid: String): Option[Album] = {
    val FIND =
      """MATCH (a:Album {uuid: $uuid})
        |RETURN a
      """.stripMargin

    val result = session.run(FIND, parameters("uuid", uuid))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def findByName(name: String): Option[Album] = {
    val FIND =
      """MATCH (a:Album {name: $name})
        |RETURN a
      """.stripMargin

    val result = session.run(FIND, parameters("name", name))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: String): Option[Album] = {
    val DELETE =
      """MATCH (a:Album {uuid: $uuid})
        |DETACH DELETE a
      """.stripMargin

    val result = session.run(DELETE, parameters("uuid", uuid))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def save(album: Album): Option[Album] = {
    val maybeAlbum = album.uuid.flatMap(findById)
    val MERGE =
      """MERGE (a:Album {uuid: $uuid, name: $name})
        |ON CREATE SET a.released = $released
        |ON MATCH SET a.released = $released
        |RETURN a
      """.stripMargin

    val params = setParams(maybeAlbum, album)

    val result = session.run(MERGE, params)

    Option(result.single()).flatMap(buildFrom)
  }

  final override protected def buildFrom(record: Record): Option[Album] = {
    for {
      node <- Option(record.get(0))
    } yield {
      Album(
        uuid = Some(node.get("uuid").asString()),
        name = node.get("name").asString(),
        releaseDate = Some(node.get("released").asString())
      )
    }
  }

  final override protected def setParams(before: Option[Album], after: Album): Value = before match {
    case Some(album) =>
      parameters(
        "uuid",
        album.uuid,
        "name",
        album.name,
        "released",
        after.releaseDate.getOrElse(album.releaseDate.getOrElse("null"))
      )
    case None =>
      val uuid = UUID.randomUUID().toString
      parameters(
        "uuid",
        uuid,
        "name",
        after.name,
        "released",
        after.releaseDate.getOrElse("null")
      )
  }
}
