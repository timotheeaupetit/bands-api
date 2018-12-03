package com.music.model.relationships.types

import com.music.model.Period

case class PlayedIn(instruments: List[String], start: Option[Int], end: Option[Int])

object PlayedIn {
  def apply(period: Period, instruments: List[String]): PlayedIn = new PlayedIn(instruments,
                                                                                start = period.start,
                                                                                end = period.end)
}
