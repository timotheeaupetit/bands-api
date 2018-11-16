package com.music.model

import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

sealed abstract class Instrument(val value: String) extends StringEnumEntry

object Instrument extends StringEnum[Instrument] {

  case object BackingVocals extends Instrument("backing vocals")

  case object Banjo extends Instrument("banjo")

  case object Bass extends Instrument("bass")

  case object Bassoon extends Instrument("bassoon")

  case object Cello extends Instrument("cello")

  case object ChapmanStick extends Instrument("Chapman stick")

  case object Clarinet extends Instrument("clarinet")

  case object Didgeridoo extends Instrument("didgeridoo")

  //contrebasse
  case object DoubleBass extends Instrument("double bass")

  case object Drums extends Instrument("drums")

  case object Flute extends Instrument("flute")

  case object Guitar extends Instrument("guitar")

  case object Harmonica extends Instrument("harmonica")

  case object Harmonium extends Instrument("harmonium")

  case object Harp extends Instrument("harp")

  //clavecin
  case object Harpsichord extends Instrument("harpsichord")

  //cor
  case object Horn extends Instrument("horn")

  //vielle
  case object HurdyGurdy extends Instrument("hurdy gurdy")

  case object Kalimba extends Instrument("kalimba")

  case object Keyboards extends Instrument("keyboards")

  case object LeadGuitar extends Instrument("lead guitar")

  case object LeadVocals extends Instrument("lead vocals")

  //hautbois
  case object Oboe extends Instrument("oboe")

  case object Percussion extends Instrument("percussion")

  case object Piano extends Instrument("piano")

  case object Programming extends Instrument("programming")

  case object RythmGuitar extends Instrument("rythm guitar")

  case object Samples extends Instrument("samples")

  case object Saxophone extends Instrument("saxophone")

  case object Strings extends Instrument("strings")

  case object Synthesizer extends Instrument("synthesizer")

  case object Trombone extends Instrument("trombone")

  case object Trumpet extends Instrument("trumpet")

  case object Tuba extends Instrument("tuba")

  //platines
  case object Turntables extends Instrument("turntables")

  case object Viola extends Instrument("viola")

  case object Violin extends Instrument("violin")

  case object Vocals extends Instrument("vocals")

  case object Xylophone extends Instrument("xylophone")

  override def values: immutable.IndexedSeq[Instrument] = findValues

}
