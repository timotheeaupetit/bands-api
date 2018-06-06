package com.music.api.model.entities.queries

import com.music.api.model.entities.Person
import org.neo4j.driver.v1.Session

class PersonQueries(session: Session) extends NodeQueries[Person] {
  override def findAll(): List[Person] = ??? /*{
    val FIND_ALL =
      """MATCH (p:Person)
        |RETURN p
      """.stripMargin

    val result = session.run(FIND_ALL)
    result.list(record => record.asInstanceOf[Person])
  }*/

  override def find(id: String): Person = ???

  override def delete(id: String): Unit = ???

  override def create(t: Person): Unit = ???

  override def update(t: Person): Unit = ???
}
