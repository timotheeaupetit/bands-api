package com.music.api.model.entities.queries

import com.music.api.model.entities.Person
import org.neo4j.driver.v1.Session

import scala.collection.JavaConverters._

class PersonQueries(session: Session) extends NodeQueries[Person] {
  override def findAll(): List[Person] = {
    val FIND_ALL =
      """MATCH (p:Person)
        |RETURN p;
      """.stripMargin

    val result = session.run(FIND_ALL)

    result
      .list[Person](
        rec => {
          val node = rec.get(0)
          Person(
            id = Some(node.get("id").asString()),
            full_name = node.get("full_name").asString(),
            first_name = Some(node.get("first_name").asString()),
            last_name = Some(node.get("last_name").asString()),
            aka = Some(node.get("aka").asString()),
            dob = Some(node.get("dob").asString())
          )
        }
      )
      .asScala
      .toList
  }

  override def find(id: String): Person = ???

  override def delete(id: String): Unit = ???

  override def create(t: Person): Unit = ???

  override def update(t: Person): Unit = ???
}
