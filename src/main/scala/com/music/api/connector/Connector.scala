package com.music.api.connector

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{HttpApp, Route}
import com.music.api.model.entities.queries.{AlbumQueries, BandQueries, PersonQueries}
import com.music.api.model.entities.types._
import com.music.api.utils.ProjectConfiguration.ProjectConfig
import com.music.api.utils.{Neo4jManager, SwaggerRoute}
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

  val routes: Route =
    pathPrefix("persons") {
      pathEnd {
        get {
          val response = personQueries.findAll().asJson
          complete(OK, ToResponseMarshallable(response))
        } ~
          post {
            entity(as[Person]) { newPerson =>
              val response = personQueries.save(newPerson).asJson
              complete(Created, ToResponseMarshallable(response))
            }
          } ~
          put {
            entity(as[Person]) { person =>
              val response = personQueries.save(person).asJson
              complete(OK, ToResponseMarshallable(response))
            }
          }
      } ~
        path(Segment) { strId =>
          val personId = strId.trim
          get {
            val response = personQueries.findById(personId).asJson
            complete(OK, ToResponseMarshallable(response))
          } ~
            delete {
              val response = personQueries.delete(personId).asJson
              complete(NoContent, ToResponseMarshallable(response))
            }
        }
    } ~
      pathPrefix("bands") {
        pathEnd {
          get {
            val response = bandQueries.findAll().asJson
            complete(OK, ToResponseMarshallable(response))
          } ~
            post {
              entity(as[Band]) { newBand =>
                val response = bandQueries.save(newBand).asJson
                complete(Created, ToResponseMarshallable(response))
              }
            } ~
            put {
              entity(as[Band]) { band =>
                val response = bandQueries.save(band).asJson
                complete(OK, ToResponseMarshallable(response))
              }
            }
        } ~
          path(Segment) { strId =>
            val bandId = strId.trim
            get {
              val response = bandQueries.findById(bandId).asJson
              complete(OK, ToResponseMarshallable(response))
            } ~
              delete {
                val response = bandQueries.delete(bandId).asJson
                complete(NoContent, ToResponseMarshallable(response))
              }
          }
      } ~
      pathPrefix("albums") {
        pathEnd {
          get {
            val response = albumQueries.findAll().asJson
            complete(OK, ToResponseMarshallable(response))
          } ~
            post {
              entity(as[Album]) { newAlbum =>
                val response = albumQueries.save(newAlbum).asJson
                complete(Created, ToResponseMarshallable(response))
              }
            } ~
            put {
              entity(as[Album]) { album =>
                val response = albumQueries.save(album).asJson
                complete(OK, ToResponseMarshallable(response))
              }
            }
        } ~
          path(Segment) { strId =>
            val albumId = strId.trim
            get {
              val response = albumQueries.findById(albumId).asJson
              complete(OK, ToResponseMarshallable(response))
            } ~
              delete {
                val response = albumQueries.delete(albumId).asJson
                complete(NoContent, ToResponseMarshallable(response))
              }
          }
      } ~ SwaggerRoute.getSwaggerRoute("swagger_band.yaml")

}
