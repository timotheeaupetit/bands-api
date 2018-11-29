package com.music.model

import com.music.model.entities.types.Album

case class BandPage(name: String,
                    formed: Formed,
                    disbanded: Disbanded,
                    members: List[Member] = List.empty,
                    albums: List[Album] = List.empty)

case class Formed(date: Option[Int], location: Option[Location])

case class Disbanded(date: Option[Int])

case class Member(full_name: String, aka: Option[String], instruments: List[String], periods: List[Period])

case class Period(start: Option[Int], end: Option[Int])

case class Location(country: String, state: Option[String], city: Option[String])
