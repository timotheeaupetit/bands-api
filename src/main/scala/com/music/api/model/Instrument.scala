package com.music.api.model

import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

sealed abstract class Instrument(val value: String) extends StringEnumEntry

object Instrument extends StringEnum[Instrument] {

  case object BackingVocals extends Instrument("backing vocals")
  case object Banjo extends Instrument("banjo")
  case object Bass extends Instrument("bass")
  case object Bassoon extends Instrument("bassoon")
  case object Cello extends Instrument("cello")
  case object Clarinet extends Instrument("clarinet")
  case object Didgeridoo extends Instrument("didgeridoo")
  case object DoubleBass extends Instrument("double bass") //contrebasse
  case object Drums extends Instrument("drums")
  case object Flute extends Instrument("flute")
  case object Guitar extends Instrument("guitar")
  case object Harmonica extends Instrument("harmonica")
  case object Harp extends Instrument("harp")
  case object Harpsichord extends Instrument("harpsichord") //clavecin
  case object Horn extends Instrument("horn") //cor
  case object HurdyGurdy extends Instrument("hurdy gurdy") //vielle
  case object Keyboards extends Instrument("keyboards")
  case object LeadVocals extends Instrument("lead vocals")
  case object Oboe extends Instrument("oboe") //hautbois
  case object Percussion extends Instrument("percussion")
  case object Piano extends Instrument("piano")
  case object Samples extends Instrument("samples")
  case object Saxophone extends Instrument("saxophone")
  case object Synthesizer extends Instrument("synthesizer")
  case object Trombone extends Instrument("trombone")
  case object Trumpet extends Instrument("trumpet")
  case object Tuba extends Instrument("tuba")
  case object Turntables extends Instrument("turntables") //platines
  case object Violin extends Instrument("violin")
  case object Vocals extends Instrument("vocals")
  case object Xylophone extends Instrument("xylophone")

  override def values: immutable.IndexedSeq[Instrument] = findValues

}
