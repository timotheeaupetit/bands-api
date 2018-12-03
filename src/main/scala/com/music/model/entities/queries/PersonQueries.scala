package com.music.model.entities.queries

import java.util.UUID

import com.music.model.entities.types.{NewPerson, Person}
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class PersonQueries(implicit session: Session) extends NodeQueries[NewPerson, Person] {

  final override def create(newPerson: NewPerson): Option[Person] = {
    val person = Person(newPerson)
    val result = session.run(PersonQueries.CREATE, PersonQueries.setParams(person))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    Try(session.run(PersonQueries.DELETE, PersonQueries.setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Person " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Person] = {
    val result = session.run(PersonQueries.FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Person] = {
    Try(session.run(PersonQueries.FIND_BY_ID, PersonQueries.setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(fullName: String): Option[Person] = {
    val result = session.run(PersonQueries.FIND_BY_NAME, parameters("full_name", fullName))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(person: Person): Option[Person] = {
    Try(session.run(PersonQueries.UPDATE, PersonQueries.setParams(person))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override protected def buildFrom(record: Record): Option[Person] = {
    for {
      node <- Option(record.get(0))
      _uuid <- Try(UUID.fromString(node.get("uuid").asString())).toOption
    } yield {
      Person(uuid = _uuid,
             fullName = node.get("full_name").asString(),
             firstName = Some(node.get("first_name").asString()),
             lastName = Some(node.get("last_name").asString()),
             aka = Some(node.get("aka").asString()),
             born = Some(node.get("born").asString()))
    }
  }

}

object PersonQueries {
  val CREATE: String =
    """MERGE (p:Person {full_name: $full_name})
      |ON CREATE SET  p.uuid = $uuid,
      |               p.first_name = $first_name,
      |               p.last_name = $last_name,
      |               p.aka = $aka,
      |               p.born = $born
      |ON MATCH SET p.first_name = $first_name,
      |             p.last_name = $last_name,
      |             p.aka = $aka,
      |             p.born = $born
      |RETURN p
    """.stripMargin

  val DELETE: String =
    """MATCH (p:Person {uuid: $uuid})
      |DETACH DELETE p
    """.stripMargin

  val FIND_ALL: String =
    """MATCH (p:Person)
      |RETURN p
      |LIMIT 50
    """.stripMargin

  val FIND_BY_ID: String =
    """MATCH (p:Person {uuid: $uuid})
      |RETURN p
    """.stripMargin

  val FIND_BY_NAME: String =
    """MATCH (p:Person {full_name: $full_name})
      |RETURN p
    """.stripMargin

  val UPDATE: String =
    """MERGE (p:Person {uuid: $uuid})
      |ON MATCH SET p.full_name = $full_name,
      |             p.first_name = $first_name,
      |             p.last_name = $last_name,
      |             p.aka = $aka,
      |             p.born = $born
      |RETURN p
    """.stripMargin

  def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)

  def setParams(person: Person): Value = {
    parameters("uuid", person.uuid.toString,
               "full_name", person.fullName,
               "first_name", person.firstName.getOrElse("null"),
               "last_name", person.lastName.getOrElse("null"),
               "aka", person.aka.getOrElse("null"),
               "born", person.born.getOrElse("null"))
  }
}
