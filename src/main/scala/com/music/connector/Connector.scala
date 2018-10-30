package com.music.connector

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{HttpApp, Route}
import com.music.model.entities.queries.{AlbumQueries, BandQueries, PersonQueries}
import com.music.model.entities.types._
import com.music.model.relationships.queries.PlayedInQueries
import com.music.model.relationships.types.PlayedIn
import com.music.utils.ProjectConfiguration.ProjectConfig
import com.music.utils.{Neo4jManager, SwaggerRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import org.neo4j.driver.v1.Session

class Connector(projectConfig: ProjectConfig) extends HttpApp {

  private val neo4jManager = new Neo4jManager(projectConfig.neo4jConfig)

  implicit val neo4jSession: Session = neo4jManager.session

  private val personQueries = new PersonQueries()
  private val bandQueries = new BandQueries()
  private val albumQueries = new AlbumQueries()

  private val playedInQueries = new PlayedInQueries()

  val routes: Route =
    pathPrefix("persons") {
      pathEnd {
        get {
          val response = personQueries.findAll().asJson
          complete(ToResponseMarshallable(response))
        } ~
          post {
            entity(as[Person]) { newPerson =>
              val response = personQueries.save(newPerson).asJson
              complete(ToResponseMarshallable(response))
            }
          } ~
          put {
            entity(as[Person]) { person =>
              val response = personQueries.save(person).asJson
              complete(ToResponseMarshallable(response))
            }
          }
      } ~
        pathPrefix(Segment) { strId =>
          val personId = strId.trim
          val maybePerson = personQueries.findById(personId)
          pathEnd {
            get {
              val response = maybePerson.asJson
              complete(ToResponseMarshallable(response))
            } ~
              delete {
                val response = personQueries.delete(personId).asJson
                complete(ToResponseMarshallable(response))
              }
          } ~
            path("bands" / Segment) { strBandId =>
              val bandId = strBandId.trim
              val maybeBand = bandQueries.findById(bandId)
              pathEnd {
                post {
                  entity(as[PlayedIn]) { playedIn =>
                    (maybePerson, maybeBand) match {
                      case (Some(person), Some(band)) =>
                        playedInQueries.link(person, band, playedIn)
                        complete(Created)
                      case _ => complete(NotFound)
                    }
                  }
                }
              }
            }
        }
    } ~
      pathPrefix("bands") {
        pathEnd {
          get {
            val response = bandQueries.findAll().asJson
            complete(ToResponseMarshallable(response))
          } ~
            post {
              entity(as[Band]) { newBand =>
                val response = bandQueries.save(newBand).asJson
                complete(ToResponseMarshallable(response))
              }
            } ~
            put {
              entity(as[Band]) { band =>
                val response = bandQueries.save(band).asJson
                complete(ToResponseMarshallable(response))
              }
            }
        } ~
          path(Segment) { strId =>
            val bandId = strId.trim
            get {
              val response = bandQueries.findById(bandId).asJson
              complete(ToResponseMarshallable(response))
            } ~
              delete {
                val response = bandQueries.delete(bandId).asJson
                complete(ToResponseMarshallable(response))
              }
          }
      } ~
      pathPrefix("albums") {
        pathEnd {
          get {
            val response = albumQueries.findAll().asJson
            complete(ToResponseMarshallable(response))
          } ~
            post {
              entity(as[Album]) { newAlbum =>
                val response = albumQueries.save(newAlbum).asJson
                complete(ToResponseMarshallable(response))
              }
            } ~
            put {
              entity(as[Album]) { album =>
                val response = albumQueries.save(album).asJson
                complete(ToResponseMarshallable(response))
              }
            }
        } ~
          path(Segment) { strId =>
            val albumId = strId.trim
            get {
              val response = albumQueries.findById(albumId).asJson
              complete(ToResponseMarshallable(response))
            } ~
              delete {
                val response = albumQueries.delete(albumId).asJson
                complete(ToResponseMarshallable(response))
              }
          }
      } ~ SwaggerRoute.getSwaggerRoute("swagger_band.yaml")

}
