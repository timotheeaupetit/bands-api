package com.music.utils

import com.music.utils.ProjectConfiguration.Neo4jAuraConfig
import org.neo4j.driver.v1.{AuthTokens, Driver, GraphDatabase, Session}

class Neo4jAuraManager(configuration: Neo4jAuraConfig) {
  private val uri = s"neo4j+s://${configuration.host}"
  private val username = configuration.user.getOrElse("")
  private val password = configuration.password.getOrElse("")

  lazy val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))

  lazy val session: Session = driver.session()
}
