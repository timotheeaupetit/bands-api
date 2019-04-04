package com.music.repository

import org.neo4j.driver.v1.Session

trait Neo4jSession {
  def session: Session
}
