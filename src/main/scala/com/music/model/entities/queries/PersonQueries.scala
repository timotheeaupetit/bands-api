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

    val CREATE =
      """MERGE (p:Person {full_name: $full_name})
        |ON CREATE SET  p.uuid = $uuid,
        |               p.first_name = $first_name,
        |               p.last_name = $last_name,
        |               p.aka = $aka,
        |               p.born = $born
        |ON MATCH SET p.uuid = $uuid,
        |             p.first_name = $first_name,
        |             p.last_name = $last_name,
        |             p.aka = $aka,
        |             p.born = $born
        |RETURN p
      """.stripMargin

    val params = setParams(person)

    val result = session.run(CREATE, params)

    Option(result.single()).flatMap(buildFrom)
  }

  final override def delete(uuid: UUID): Option[Throwable] = {
    val DELETE =
      """MATCH (p:Person {uuid: $uuid})
        |DETACH DELETE p
      """.stripMargin

    Try(session.run(DELETE, setIdParam(uuid))) match {
      case Success(_)         => None
      case Failure(exception) => throw new NoSuchElementException(exception + ": Person " + uuid + " doesn't exist.")
    }
  }

  final override def findAll(): List[Person] = {
    val FIND_ALL =
      """MATCH (p:Person)
        |RETURN p
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result.asScala.toList.flatten(buildFrom)
  }

  final override def findById(uuid: UUID): Option[Person] = {
    val FIND =
      """MATCH (p:Person {uuid: $uuid})
        |RETURN p
      """.stripMargin

    Try(session.run(FIND, setIdParam(uuid))) match {
      case Success(result) => Option(result.single()).flatMap(buildFrom)
      case Failure(_)      => None
    }
  }

  final override def findByName(fullName: String): Option[Person] = {
    val FIND =
      """MATCH (p:Person {full_name: $full_name})
        |RETURN p
      """.stripMargin

    val result = session.run(FIND, parameters("full_name", fullName))

    Option(result.single()).flatMap(buildFrom)
  }

  final override def update(person: Person): Option[Person] = {
    val MERGE =
      """MERGE (p:Person {uuid: $uuid})
        |ON MATCH SET p.full_name = $full_name,
        |             p.first_name = $first_name,
        |             p.last_name = $last_name,
        |             p.aka = $aka,
        |             p.born = $born
        |RETURN p
      """.stripMargin

    val params = setParams(person)

    Try(session.run(MERGE, params)) match {
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

  final override def setParams(person: Person): Value = {
    parameters("uuid", person.uuid.toString,
               "full_name", person.fullName,
               "first_name", person.firstName.getOrElse("null"),
               "last_name", person.lastName.getOrElse("null"),
               "aka", person.aka.getOrElse("null"),
               "born", person.born.getOrElse("null"))
  }

  final override protected def setIdParam(uuid: UUID): Value = parameters("uuid", uuid.toString)
}
