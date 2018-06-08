package com.music.api.model.entities.types

case class Person(id: Option[String],
                  full_name: String,
                  first_name: Option[String],
                  last_name: Option[String],
                  aka: Option[String],
                  dob: Option[String])
