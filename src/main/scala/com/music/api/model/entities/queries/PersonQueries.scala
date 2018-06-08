package com.music.api.model.entities.queries

import com.music.api.model.entities.types.Person
import org.neo4j.driver.v1.{Record, Session}

import scala.collection.JavaConverters._

class PersonQueries(session: Session) extends NodeQueries[Person] {
  final override def findAll(): List[Person] = {
    val FIND_ALL =
      """MATCH (p:Person)
        |RETURN p;
      """.stripMargin

    val result = session.run(FIND_ALL)

    result
      .list[Person](record => buildFrom(record))
      .asScala
      .toList
  }

  final override def find(id: String): Person = {
    val FIND =
      s"""MATCH (p:Person)
         |WHERE ID(p) = $id
         |RETURN p;
      """.stripMargin

    val result = session.run(FIND)

    buildFrom(result.single())
  }

  final override def delete(id: String): Unit = ???

  final override def create(person: Person): Person = {
    val CREATE =
      """MATCH (p:Person)
        |WHERE p.full_name = {fullName}
        |MERGE (p)
        |ON MATCH SET p.first_name= {first_name}
        |RETURN p
      """.stripMargin

//    val parameters = Map(
//      "fullName" -> t.full_name,
//      "firstName" -> t.first_name,
//      "lastName" -> t.last_name,
//      "aka" -> t.aka,
//      "dob" -> t.dob
//    ).asJava

//    val result = session.run(CREATE, parameters)
    val result = session.run(CREATE)

    buildFrom(result.single())
  }

  final override def update(person: Person): Person = ???

  final override protected def buildFrom(record: Record): Person = {
    val node = record.get(0)
    Person(
      id = Some(node.get("id").asString()),
      full_name = node.get("full_name").asString(),
      first_name = Some(node.get("first_name").asString()),
      last_name = Some(node.get("last_name").asString()),
      aka = Some(node.get("aka").asString()),
      dob = Some(node.get("dob").asString())
    )
  }
}
