package com.music.model.relationships.types

case class PlayedIn(instruments: List[String], start: Option[Int], end: Option[Int])
