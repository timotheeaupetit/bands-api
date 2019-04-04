package com.music

import java.util.UUID

import com.music.model.entities.types.{Band, NewBand}
import com.music.utils.ProjectConfiguration.AppConfig
import com.music.utils.ProjectConfiguration.Neo4jConfig
import com.music.utils.ProjectConfiguration.ProjectConfig

object Builder {
  private val appConfig: AppConfig = AppConfig(application = "APPLICATION", env = "env", port = 1234, app_home = "home")
  private val neo4jConfig: Neo4jConfig = Neo4jConfig(db = "DB",
                                                     host = "host",
                                                     port = 4321,
                                                     user = None,
                                                     password = None)

  val projectConfigurationBuilder: ProjectConfig = ProjectConfig(appConfig, neo4jConfig)

  def newBandBuilder(name: String = "FakeBand",
                     country: Option[String] = Some("MusicLand"),
                     formed: Option[Int] = Some(1956),
                     disbanded: Option[Int] = Some(2003)): NewBand = NewBand(name = name,
                                                                             country = country,
                                                                             formed = formed,
                                                                             disbanded = disbanded)

  def bandBuilder(uuid: UUID = UUID.randomUUID(),
                  name: String = "FakeBand",
                  country: Option[String] = Some("MusicLand"),
                  formed: Option[Int] = Some(1956),
                  disbanded: Option[Int] = Some(2003)): Band = new Band(uuid = uuid,
                                                                        name = name,
                                                                        country = country,
                                                                        formed = formed,
                                                                        disbanded = disbanded)
}
