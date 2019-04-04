package com.music.repository

import org.neo4j.driver.v1.Session
import org.specs2.mock.Mockito

trait MockNeo4jSession extends Neo4jSession with Mockito {
  override def session: Session = mock[Session]
}
