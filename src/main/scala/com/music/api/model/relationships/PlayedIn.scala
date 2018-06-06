package com.music.api.model.relationships

import com.music.api.model.entities.{Band, Person}

case class PlayedIn(person: Person, band: Band, instrument: Option[String], start: Option[String], end: Option[String])
