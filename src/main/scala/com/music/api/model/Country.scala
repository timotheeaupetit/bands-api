package com.music.api.model

import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

sealed abstract class Country(val value: String) extends StringEnumEntry

object Countries extends StringEnum[Country] {

  case object Algeria extends Country("Algeria")
  case object Argentina extends Country("Argentina")
  case object Austria extends Country("Austria")
  case object Australia extends Country("Australia")
  case object Belgium extends Country("Belgium")
  case object Bolivia extends Country("Bolivia")
  case object Brazil extends Country("Brazil")
  case object Canada extends Country("Canada")
  case object Chile extends Country("Chile")
  case object China extends Country("China")
  case object Colombia extends Country("Colombia")
  case object CzechRepublic extends Country("Czech Republic")
  case object Denmark extends Country("Denmark")
  case object Estonia extends Country("Estonia")
  case object Finland extends Country("Finland")
  case object France extends Country("France")
  case object Germany extends Country("Germany")
  case object Greece extends Country("Greece")
  case object India extends Country("India")
  case object Ireland extends Country("Ireland")
  case object Italy extends Country("Italy")
  case object Japan extends Country("Japan")
  case object Latvia extends Country("Latvia")
  case object Lithuania extends Country("Lithuania")
  case object Luxembourg extends Country("Luxembourg")
  case object Mexico extends Country("Mexico")
  case object Morocco extends Country("Morocco")
  case object Netherlands extends Country("Netherlands")
  case object NewZealand extends Country("New Zealand")
  case object Norway extends Country("Norway")
  case object Poland extends Country("Poland")
  case object Portugal extends Country("Portugal")
  case object Russia extends Country("Russia")
  case object SouthAfrica extends Country("South Africa")
  case object SouthKorea extends Country("South Korea")
  case object Spain extends Country("Spain")
  case object Sweden extends Country("Sweden")
  case object Switzerland extends Country("Switzerland")
  case object Tunisia extends Country("Tunisia")
  case object UK extends Country("UK")
  case object USA extends Country("USA")
  case object Venezuela extends Country("Venezuela")

  override def values: immutable.IndexedSeq[Country] = findValues

}
