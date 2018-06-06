package com.music.api.utils

import akka.http.scaladsl.model.StatusCodes.PermanentRedirect
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path.{Empty, SingleSlash}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileAndResourceDirectives.getFromResource
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success, Try}

object SwaggerRoute {

  private val redirectUri = "index.html?url=/swagger.yaml"

  private val swaggerPrefix = "swagger"

  private val swaggerWebJar = "swagger-ui"

  def getSwaggerRoute(swaggerFile: String): Route = {
    val webJarAssetLocator = new WebJarAssetLocator

    path("swagger.yaml") {
      getFromResource(swaggerFile)
    } ~
      pathPrefix(swaggerPrefix) {
        extractUnmatchedPath { path =>
          if (path == Empty || path == SingleSlash) {
            redirect(Uri(s"/$swaggerPrefix/$redirectUri"), PermanentRedirect)
          } else {
            Try(webJarAssetLocator.getFullPath(swaggerWebJar, path.toString)) match {
              case Success(fullPath) =>
                getFromResource(fullPath)
              case Failure(_: IllegalArgumentException) =>
                reject
              case Failure(e) =>
                failWith(e)
            }
          }
        }
      }
  }
}
