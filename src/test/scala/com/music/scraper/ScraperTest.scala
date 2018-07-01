package com.music.scraper

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.specs2.Specification
import org.specs2.matcher.MatchResult

class ScraperTest extends Specification {
  def is =
    s2"""This is the Scraper specification
            The year the band was formed should be '1999'       $bandFormation
            There should be 11 members extracted from 'content' $numberOfMembers
            There should be 'Maynard James Keenan' among them   $maynard
      """

  private val browser = JsoupBrowser()

  private val rawContent = """<div class="artist_info">
              | <div class="info_hdr">
              |  Formed
              | </div>
              | <div class="info_content">
              |   1999,
              |  <a class="location" href="/location/Los Angeles/CA/United States">Los Angeles, CA, United States</a>
              | </div>
              | <div class="info_hdr">
              |  Members
              | </div>
              | <div class="info_content">
              |  <a title="[Artist579837]" href="/artist/maynard-james-keenan" class="artist">Maynard James Keenan</a> (lead vocals, piano, kalimba, guitar),
              |  <a title="[Artist955887]" href="/artist/billy-howerdel" class="artist">Billy Howerdel</a> (lead guitar, bass, vocals, keyboards, piano, harmonium, programming),
              |  <a title="[Artist303942]" href="/artist/tim-alexander" class="artist">Tim Alexander</a> (drums, percussion, 1999),
              |  <a title="[Artist52219]" href="/artist/josh-freese-1" class="artist">Josh Freese</a> (drums, percussion, backing vocals, 1999-2011),
              |  <a title="[Artist58981]" href="/artist/paz_lenchantin" class="artist">Paz Lenchantin</a> (bass, strings, violin, keyboards, piano, backing vocals, 1999-2001, 2004),
              |  <a title="[Artist976851]" href="/artist/troy_van_leeuwen" class="artist">Troy Van Leeuwen</a> (rhythm guitar, 1999-2003), Jeordie White [aka
              |  <a title="[Artist429142]" href="/artist/twiggy-ramirez" class="artist">Twiggy Ramirez</a>] (bass, backing vocals, 2003-04),
              |  <a title="[Artist467318]" href="/artist/danny-lohner" class="artist">Danny Lohner</a> (rhythm guitar, bass, keyboards, backing vocals, programming, 2003, 2004),
              |  <a title="[Artist744]" href="/artist/james-iha" class="artist">James Iha</a> (rhythm guitar, keyboards, backing vocals, programming, 2003-04, 2010-present),
              |  <a title="[Artist1290108]" href="/artist/matt-mcjunkins" class="artist">Matt McJunkins</a> (bass, backing vocals, 2010-present),
              |  <a title="[Artist1290111]" href="/artist/jeff-friedl" class="artist">Jeff Friedl</a> (drums, 2011-present)
              | </div>
              | <div class="info_hdr">
              |  Related Artists
              | </div>
              | <div class="info_content">
              |  <a title="[Artist1044021]" href="/artist/the-beta-machine" class="artist">The Beta Machine</a>,
              |  <a title="[Artist127008]" href="/artist/black-light-burns" class="artist">Black Light Burns</a>,
              |  <a title="[Artist221369]" href="/artist/the_damning_well" class="artist">The Damning Well</a>,
              |  <a title="[Artist19883]" href="/artist/the_desert_sessions" class="artist">The Desert Sessions</a>,
              |  <a title="[Artist294739]" href="/artist/into-the-presence" class="artist">Into the Presence</a>,
              |  <a title="[Artist225]" href="/artist/nine-inch-nails-1" class="artist">Nine Inch Nails</a>
              | </div>
              |</div>""".stripMargin

  private val content = browser.parseString(rawContent)

  private val band = Scraper.getBand

  private val members = Scraper.getMembers(content)

  private def bandFormation: MatchResult[Option[String]] = band.formed must beSome("1999")

  private def numberOfMembers: MatchResult[Int] = members.length must beEqualTo(11)

  private def maynard = members.count(_.full_name == "Maynard James Keenan") must beEqualTo(1)
}
