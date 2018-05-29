package com.music

import scala.util.Properties

trait EnvironmentVariable {

  def name: String

  override def toString: String = name

}

case object APP_HOME extends EnvironmentVariable { val name = "APP_HOME" }
case object ENV_NAME extends EnvironmentVariable { val name = "ENV_NAME" }
case object APPLICATION extends EnvironmentVariable { val name = "APPLICATION" }
case object PORT extends EnvironmentVariable { val name = "PORT" }

case object NEO4J_HOST extends EnvironmentVariable { val name = "NEO4J_HOST" }
case object NEO4J_PORT extends EnvironmentVariable { val name = "NEO4J_PORT" }
case object NEO4J_USER extends EnvironmentVariable { val name = "NEO4J_USER" }
case object NEO4J_PASSWORD extends EnvironmentVariable { val name = "NEO4J_PASSWORD" }

object EnvironmentVariable {

  def envOrError(environmentVariable: EnvironmentVariable): Either[Error, String] = {
    Properties
      .envOrNone(s"$environmentVariable")
      .toRight(new Error(s"$environmentVariable environment variable is not defined"))
  }

  def envOrElse(e: EnvironmentVariable, alt: String): String = {
    envOrError(e)
      .fold(
        _ => alt,
        identity
      )
  }

  def requireEnv(e: EnvironmentVariable): String = {
    Properties.envOrNone(e.name).getOrElse(throw new RuntimeException(s"Missing ${e.name}"))
  }
}
