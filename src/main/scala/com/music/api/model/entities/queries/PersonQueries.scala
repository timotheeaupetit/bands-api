package com.music.api.model.entities.queries

import java.util.UUID

import com.music.api.model.entities.types.Person
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.{Record, Session, Value}

import scala.collection.JavaConverters._
import scala.util.Try

class PersonQueries(implicit session: Session) extends NodeQueries[Person] {
  final override def findAll(): List[Person] = {
    val FIND_ALL =
      """MATCH (p:Person)
        |RETURN p
        |LIMIT 50
      """.stripMargin

    val result = session.run(FIND_ALL)

    result
      .list[Person](buildFrom)
      .asScala
      .toList
  }

  final override def findById(uuid: String): Option[Person] = {
    val FIND =
      """MATCH (p:Person {uuid: $uuid})
        |RETURN p
      """.stripMargin

    val result = session.run(FIND, parameters("uuid", uuid))

    Try(buildFrom(result.single())).toOption
  }

  final override def findByName(fullName: String): Option[Person] = {
    val FIND =
      """MATCH (p:Person {full_name: $full_name})
        |RETURN p
      """.stripMargin

    val result = session.run(FIND, parameters("full_name", fullName))

    Try(buildFrom(result.single())).toOption
  }

  final override def delete(uuid: String): Option[Person] = {
    val DELETE =
      """MATCH (p:Person {uuid: $uuid})
        |DETACH DELETE p
      """.stripMargin

    val result = session.run(DELETE, parameters("uuid", uuid))

    Try(buildFrom(result.single())).toOption
  }

  final override def save(person: Person): Person = {
    val maybePerson = person.uuid.flatMap(findById)
    val MERGE =
      """MERGE (p:Person {uuid: $uuid, full_name: $fullName})
        |ON CREATE SET p.first_name = $firstName, p.last_name = $lastName, p.aka = $aka, p.born = $born
        |ON MATCH SET p.first_name = $firstName, p.last_name = $lastName, p.aka = $aka, p.born = $born
        |RETURN p
      """.stripMargin

    val params = setParams(maybePerson, person)

    val result = session.run(MERGE, params)

    buildFrom(result.single())
  }

  final override protected def buildFrom(record: Record): Person = {
    val node = record.get(0)
    Person(
      uuid = Some(node.get("uuid").asString()),
      full_name = node.get("full_name").asString(),
      first_name = Some(node.get("first_name").asString()),
      last_name = Some(node.get("last_name").asString()),
      aka = Some(node.get("aka").asString()),
      born = Some(node.get("born").asString())
    )
  }

  final override protected def setParams(before: Option[Person], after: Person): Value = before match {
    case Some(person) =>
      parameters(
        "uuid",
        person.uuid,
        "fullName",
        person.full_name,
        "firstName",
        after.first_name.getOrElse(person.first_name.getOrElse("null")),
        "lastName",
        after.last_name.getOrElse(person.last_name.getOrElse("null")),
        "aka",
        after.aka.getOrElse(person.aka.getOrElse("null")),
        "born",
        after.born.getOrElse(person.born.getOrElse("null"))
      )
    case None =>
      val uuid = UUID.randomUUID().toString
      parameters(
        "uuid",
        uuid,
        "fullName",
        after.full_name,
        "firstName",
        after.first_name.getOrElse("null"),
        "lastName",
        after.last_name.getOrElse("null"),
        "aka",
        after.aka.getOrElse("null"),
        "born",
        after.born.getOrElse("null")
      )
  }
}
