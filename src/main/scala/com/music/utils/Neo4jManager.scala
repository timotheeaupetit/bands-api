package com.music.utils

import com.music.utils.ProjectConfiguration.Neo4jConfig
import org.neo4j.driver.v1._

class Neo4jManager(configuration: Neo4jConfig) {
  private val uri = s"bolt://${configuration.host}:${configuration.port}"
  private val username = configuration.user.getOrElse("")
  private val password = configuration.password.getOrElse("")

  private lazy val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))

  lazy val session: Session = driver.session()
}
