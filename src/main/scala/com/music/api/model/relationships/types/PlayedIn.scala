package com.music.api.model.relationships.types

import com.music.api.model.entities.types.{Band, Person}

case class PlayedIn(person: Person, band: Band, instrument: Option[String], start: Option[String], end: Option[String])
