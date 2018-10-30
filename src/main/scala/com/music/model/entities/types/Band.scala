package com.music.model.entities.types

case class Band(uuid: Option[String],
                name: String,
                aka: Option[String],
                country: Option[String],
                formed: Option[String],
                disbanded: Option[String])
