package com.music.model.entities.types

case class Person(uuid: Option[String],
                  full_name: String,
                  first_name: Option[String],
                  last_name: Option[String],
                  aka: Option[String],
                  born: Option[String])
