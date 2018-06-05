package com.music.model.entities

case class Band(id: Option[String],
                name: String,
                aka: Option[String],
                country: Option[String],
                formed: Option[String],
                disbanded: Option[String])
