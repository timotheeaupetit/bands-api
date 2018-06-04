package com.music.utils

import org.neo4j.driver.v1.{Driver, GraphDatabase, Session}

trait Neo4jSession {

  def session: Session = Neo4jSession.session

}

object Neo4jSession {

  private lazy val driver: Driver = GraphDatabase.driver("localhost")

  lazy val session: Session = driver.session()

}
