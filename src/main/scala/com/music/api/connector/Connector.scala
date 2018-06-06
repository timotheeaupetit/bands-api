package com.music.api.connector

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{HttpApp, Route}
import com.music.api.model.entities.queries.PersonQueries
import com.music.api.utils.ProjectConfiguration.ProjectConfig
import com.music.api.utils.{Neo4jManager, SwaggerRoute}

class Connector(projectConfig: ProjectConfig) extends HttpApp {

//  implicit val system: ActorSystem = ActorSystem("Sonemic")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//
//  val log = Logging(system, this.getClass)

  private val neo4jManager = new Neo4jManager(projectConfig.neo4jConfig)
  val personQueries = new PersonQueries(neo4jManager.session)

  val routes: Route =
    pathPrefix("persons") {
      pathEnd {
        get {
          complete(OK)
        } ~
          post {
            complete(Created)
          }
      } ~
        path(Segment) { personId =>
          get {
            complete(OK)
          } ~
            put {
              complete(OK)
            } ~
            delete {
              complete(NoContent)
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
            }
        } ~
          path(Segment) { bandId =>
            get {
              complete(OK)
            } ~
              put {
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
            }
        } ~
          path(Segment) { albumId =>
            get {
              complete(OK)
            } ~
              put {
                complete(OK)
              } ~
              delete {
                complete(NoContent)
              }
          }
      } ~ SwaggerRoute.getSwaggerRoute("swagger_band.yaml")

}
