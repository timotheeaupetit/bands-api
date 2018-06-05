package com.music.model.relationships

import com.music.model.entities.{Band, Person}

case class PlayedIn(person: Person, band: Band, instrument: Option[String], start: Option[String], end: Option[String])
