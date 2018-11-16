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
      pathPrefix(JavaUUID) { personId =>
        val maybePerson = personQueries.findById(personId)
        pathEnd {
          get {
            maybePerson match {
              case Some(response) => complete(ToResponseMarshallable(response.asJson))
              case None           => complete(NotFound)
              case _              => complete(InternalServerError)
            }
          } ~
            delete {
              personQueries.delete(personId) match {
                case None    => complete(NoContent)
                case Some(_) => complete(NotFound)
                case _       => complete(InternalServerError)
              }
            }
        } ~
          path("bands" / JavaUUID) { bandId =>
            val maybeBand = bandQueries.findById(bandId)
            post {
              entity(as[PlayedIn]) { playedIn =>
                (maybePerson, maybeBand) match {
                  case (Some(person), Some(band)) =>
                    playedInQueries.link(person, band, playedIn)
                    complete(Created)
                  case _                          => complete(NotFound)
                }
              }
            }
          }
      } ~
        parameterSeq { params =>
          val parameters = params.toMap
          val maybeFullName = parameters.get("full-name")
          val maybeFirstName = parameters.get("first-name")
          val maybeLastName = parameters.get("last-name")
          val maybeBirthDate = parameters.get("born")

          get {
            val response = personQueries.findAll().asJson
            complete(ToResponseMarshallable(response))
          }
        } ~
        pathEnd {
          post {
            entity(as[NewPerson]) { newPerson =>
              val response = personQueries.create(newPerson).asJson
              complete(ToResponseMarshallable(response))
            }
          } ~
            put {
              entity(as[Person]) { person =>
                personQueries.update(person) match {
                  case Some(response) => complete(ToResponseMarshallable(response.asJson))
                  case None           => complete(NotFound)
                  case _              => complete(InternalServerError)
                }
              }
            }
        }
    } ~
      pathPrefix("bands") {
        path(JavaUUID) { bandId =>
          get {
            bandQueries.findById(bandId) match {
              case Some(response) => complete(ToResponseMarshallable(response.asJson))
              case None           => complete(NotFound)
              case _              => complete(InternalServerError)
            }
          } ~
            delete {
              bandQueries.delete(bandId) match {
                case None    => complete(NoContent)
                case Some(_) => complete(NotFound)
                case _       => complete(InternalServerError)
              }
            }
        } ~
          parameterSeq { params =>
            val parameters = params.toMap
            val maybeName = parameters.get("name")

            get {
              val response = bandQueries.findAll().asJson
              complete(ToResponseMarshallable(response))
            }
          } ~
          pathEnd {
            post {
              entity(as[NewBand]) { newBand =>
                val response = bandQueries.create(newBand).asJson
                complete(ToResponseMarshallable(response))
              }
            } ~
              put {
                entity(as[Band]) { band =>
                  bandQueries.update(band) match {
                    case Some(response) => complete(ToResponseMarshallable(response.asJson))
                    case None           => complete(NotFound)
                    case _              => complete(InternalServerError)
                  }

                }
              }
          }
      } ~
      pathPrefix("albums") {
        path(JavaUUID) { albumId =>
          get {
            albumQueries.findById(albumId) match {
              case Some(response) => complete(ToResponseMarshallable(response.asJson))
              case None           => complete(NotFound)
              case _              => complete(InternalServerError)
            }
          } ~
            delete {
              albumQueries.delete(albumId) match {
                case None    => complete(NoContent)
                case Some(_) => complete(NotFound)
                case _       => complete(InternalServerError)
              }
            }
        } ~
          parameterSeq { params =>
            val parameters = params.toMap
            val maybeStart = parameters.get("start")
            val maybeEnd = parameters.get("end")

            get {
              val response = albumQueries.findAll().asJson
              complete(ToResponseMarshallable(response))
            }
          } ~
          pathEnd {
            post {
              entity(as[NewAlbum]) { newAlbum =>
                val response = albumQueries.create(newAlbum).asJson
                complete(ToResponseMarshallable(response))
              }
            } ~
              put {
                entity(as[Album]) { album =>
                  albumQueries.update(album) match {
                    case Some(response) => complete(ToResponseMarshallable(response.asJson))
                    case None           => complete(NotFound)
                    case _              => complete(InternalServerError)
                  }
                }
              }

          }
      } ~
      path("band-page") {
        post {
          complete(OK)
        }
      } ~ SwaggerRoute.getSwaggerRoute("swagger_band.yaml")

}
