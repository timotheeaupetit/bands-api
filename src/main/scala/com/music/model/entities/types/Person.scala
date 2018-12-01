package com.music.model.entities.types

import java.util.UUID

case class Person(uuid: UUID,
                  fullName: String,
                  firstName: Option[String],
                  lastName: Option[String],
                  aka: Option[String],
                  born: Option[String])

object Person {
  def apply(uuid: UUID,
            fullName: String,
            firstName: Option[String],
            lastName: Option[String],
            aka: Option[String],
            born: Option[String]): Person = new Person(uuid, fullName, firstName, lastName, aka, born)

  def apply(newPerson: NewPerson): Person = new Person(uuid = UUID.randomUUID(),
                                                       fullName = newPerson.full_name,
                                                       firstName = newPerson.first_name,
                                                       lastName = newPerson.last_name,
                                                       aka = newPerson.aka,
                                                       born = newPerson.born)
}

case class NewPerson(full_name: String,
                     first_name: Option[String],
                     last_name: Option[String],
                     aka: Option[String],
                     born: Option[String])
