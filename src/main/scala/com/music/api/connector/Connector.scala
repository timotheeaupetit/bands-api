package com.music.api.connector

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{HttpApp, Route}
import com.music.api.model.entities.queries.PersonQueries
import com.music.api.model.entities.types._
import com.music.api.utils.ProjectConfiguration.ProjectConfig
import com.music.api.utils.{Neo4jManager, SwaggerRoute}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._

class Connector(projectConfig: ProjectConfig) extends HttpApp {

  private val neo4jManager = new Neo4jManager(projectConfig.neo4jConfig)
  private val personQueries = new PersonQueries(neo4jManager.session)

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
            val bandId = strId.trim
            get {
              complete(OK)
            } ~
              delete {
                complete(NoContent)
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
