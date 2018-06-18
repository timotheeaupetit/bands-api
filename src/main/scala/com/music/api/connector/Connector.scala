package com.music.api.connector

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{HttpApp, Route}
import com.music.api.model.entities.queries.{BandQueries, PersonQueries}
import com.music.api.model.entities.types._
import com.music.api.utils.ProjectConfiguration.ProjectConfig
import com.music.api.utils.{Neo4jManager, SwaggerRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._

class Connector(projectConfig: ProjectConfig) extends HttpApp {

  private val neo4jManager = new Neo4jManager(projectConfig.neo4jConfig)
  private val personQueries = new PersonQueries(neo4jManager.session)
  private val bandQueries = new BandQueries(neo4jManager.session)

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
        path(Segment) { strId =>
          val personId = strId.trim
          get {
            val response = personQueries.findById(personId).asJson
            complete(ToResponseMarshallable(response))
          } ~
            delete {
              val response = personQueries.delete(personId).asJson
              complete(ToResponseMarshallable(response))
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
            complete(OK)
          } ~
            post {
              complete(Created)
            } ~
            put {
              complete(OK)
            }
        } ~
          path(Segment) { strId =>
            val albumId = strId.trim
            get {
              complete(OK)
            } ~
              delete {
                complete(NoContent)
              }
          }
      } ~ SwaggerRoute.getSwaggerRoute("swagger_band.yaml")

}
