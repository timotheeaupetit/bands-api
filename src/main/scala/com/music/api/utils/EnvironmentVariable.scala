package com.music.api.utils

import java.nio.file.{FileSystem, FileSystems, Path}
import java.util.StringTokenizer

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._
import cats.{Monoid, Semigroup, SemigroupK, Show, Traverse}
import com.music.api.utils.EnvironmentVariables._

import scala.util.{Properties, Success, Try}

trait EnvironmentVariables {

  sealed abstract class EnvironmentVariable(val name: String) {
    override def toString: String = name
  }

  type ValidationResult[A] = ValidatedNel[ConfigError, A]

  sealed trait ParseConfig[A] extends (String => Try[A])

  implicit val intParseConfig: ParseConfig[Int] = new ParseConfig[Int] {
    def apply(str: String) = Try(Integer.parseInt(str))
  }

  implicit val stringParseConfig: ParseConfig[String] = new ParseConfig[String] {
    def apply(str: String) = Try(str)
  }

  implicit def listParseConfig[A](implicit aParseConfig: ParseConfig[A]): ParseConfig[List[A]] =
    new ParseConfig[List[A]] {
      override def apply(s: String): Try[List[A]] = {
        val iter: Iterator[String] = new Iterator[String] {
          val strToken = new StringTokenizer(s, ",")
          override def hasNext: Boolean = strToken.hasMoreTokens

          override def next(): String = strToken.nextToken()
        }
        // Traverse[Iterator].foldMapM[Try,List,A](iter)(aParseConfig(_))
        iter.foldLeft[Try[List[A]]](Success(List.empty[A])) { (acc, str) =>
          acc.flatMap((l: List[A]) => aParseConfig(str).map(e => e :: l))
        }
      }
    }

  implicit def nonEmptyListParseConfig[A](implicit aParseConfig: ParseConfig[List[A]]): ParseConfig[NonEmptyList[A]] =
    new ParseConfig[NonEmptyList[A]] {
      override def apply(s: String): Try[NonEmptyList[A]] =
        aParseConfig(s).flatMap[NonEmptyList[A]](l => Try(NonEmptyList.fromListUnsafe[A](l)))
    }

  def pathParseConfig(fs: FileSystem): ParseConfig[Path] = new ParseConfig[Path] {
    def apply(str: String) = Try { fs.getPath(str) }
  }

  implicit val nelSemigroup: Semigroup[NonEmptyList[ConfigError]] = SemigroupK[NonEmptyList].algebra[ConfigError]

  implicit val showEnvironmentVariable: Show[EnvironmentVariable] = Show.fromToString[EnvironmentVariable]

  implicit val showThrowable: Show[Throwable] = Show.fromToString[Throwable]

  implicit val showConfigError: Show[ConfigError] = Show.show[ConfigError] {
    case MissingVariablesError(envVar) =>
      "Missing External Variables " + showEnvironmentVariable.show(envVar)

    case ParsingError(envVar, t) =>
      "Parsing error for " + showEnvironmentVariable.show(envVar) + " : " + showThrowable.show(t)
  }

  implicit def showNelList[A](implicit showA: Show[A]): Show[NonEmptyList[A]] = Show.show[NonEmptyList[A]] {
    nel: NonEmptyList[A] =>
      val stringMonoid: Monoid[String] = new Monoid[String] {
        def empty = ""
        def combine(a1: String, a2: String): String = {
          a1 + "\n" + a2
        }
      }
      Traverse[NonEmptyList].foldMap[A, String](nel)(a => showA.show(a))(stringMonoid)
  }

  implicit val showNelConfigError: Show[NonEmptyList[ConfigError]] = showNelList[ConfigError]

  def option[A](envVar: EnvironmentVariable)(implicit f: ParseConfig[A]): ValidationResult[Option[A]] = {
    Properties
      .envOrNone(envVar.name)
      .map { value =>
        f(value).fold[Validated[ConfigError, Option[A]]](
          (t: Throwable) => Validated.invalid[ConfigError, Option[A]](ParsingError(envVar, t)),
          (a: A) => Validated.valid[ConfigError, Option[A]](Some(a))
        )
      }
      .getOrElse[Validated[ConfigError, Option[A]]](Validated.valid[ConfigError, Option[A]](None))
  }.toValidatedNel

  def require[A](envVar: EnvironmentVariable)(implicit f: ParseConfig[A]): ValidationResult[A] = {
    Properties
      .envOrNone(envVar.name)
      .map { value =>
        f(value).fold[Validated[ConfigError, A]](
          (t: Throwable) => Validated.invalid[ConfigError, A](ParsingError(envVar, t)),
          a => Validated.valid[ConfigError, A](a))
      }
      .getOrElse[Validated[ConfigError, A]](Validated.invalid[ConfigError, A](MissingVariablesError(envVar)))
  }.toValidatedNel

  def requirePath(envVar: EnvironmentVariable, fs: FileSystem): ValidationResult[Path] = {
    require[Path](envVar)(pathParseConfig(fs))
  }

  sealed trait ConfigError
  case class MissingVariablesError(environmentVariable: EnvironmentVariable) extends ConfigError
  case class ParsingError(environmentVariable: EnvironmentVariable, throwable: Throwable) extends ConfigError
}

trait Neo4jConfiguration {
  case object NEO4J_DB extends EnvironmentVariable("NEO4J_DB")
  case object NEO4J_HOST extends EnvironmentVariable("NEO4J_HOST")
  case object NEO4J_PORT extends EnvironmentVariable("NEO4J_PORT")
  case object NEO4J_USER extends EnvironmentVariable("NEO4J_USER")
  case object NEO4J_PASSWORD extends EnvironmentVariable("NEO4J_PASSWORD")

  case class Neo4jConfig(db: String, host: String, port: Int, user: Option[String], password: Option[String])

  def fNeo4jConfig: ValidationResult[Neo4jConfig] = {
    (require[String](NEO4J_DB),
      require[String](NEO4J_HOST),
      require[Int](NEO4J_PORT),
      option[String](NEO4J_USER),
      option[String](NEO4J_PASSWORD)).mapN(Neo4jConfig)
  }
}

trait ApplicationConfiguration {
  case object APP_HOME extends EnvironmentVariable("APP_HOME")
  case object PORT extends EnvironmentVariable("PORT")
  case object APPLICATION extends EnvironmentVariable("APPLICATION")
  case object ENV_NAME extends EnvironmentVariable("ENV_NAME")

  case class AppConfig(application: String, env: String, port: Int, app_home: String)

  def fApplicationConfig: ValidationResult[AppConfig] = {
    (require[String](APPLICATION),
      require[String](ENV_NAME),
      require[Int](PORT),
      require[String](APP_HOME)).mapN(AppConfig)
  }
}

trait ProjectConfiguration extends ApplicationConfiguration with Neo4jConfiguration {

  case class ProjectConfig(appConfig: AppConfig, neo4jConfig: Neo4jConfig)

  def projectConfiguration(): ValidationResult[ProjectConfig] = {
    projectConfiguration(FileSystems.getDefault)
  }

  def projectConfiguration(fs: FileSystem): ValidationResult[ProjectConfig] = {
    implicit val pathParseConfig: ParseConfig[Path] = new ParseConfig[Path] {
      def apply(str: String): Try[Path] = Try {
        fs.getPath(str)
      }
    }

    (fApplicationConfig, fNeo4jConfig).mapN(ProjectConfig)
  }
}

object ProjectConfiguration extends ProjectConfiguration

object EnvironmentVariables extends EnvironmentVariables
