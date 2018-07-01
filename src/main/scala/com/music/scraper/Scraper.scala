package com.music.scraper

import com.music.api.model.entities.types.{Band, Person}
import com.music.api.utils.ProjectConfiguration._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

class Scraper(configuration: ProjectConfig) {

  def inferBandUrl(bandName: String): String = {
    val baseUrl = configuration.miscConfig.srcUrl

    baseUrl + normalizeBandName(bandName)
  }

  final def normalizeBandName(bandName: String): String = {
    bandName.toLowerCase
      .replace("&", "and")
      .replace("(", "")
      .replace(")", "")
      .map {
        case ' ' | '¿' | '?' | '¡' | '!' => '_'
        case 'á' | 'à' | 'ä' | 'â' | 'å' => 'a'
        case 'é' | 'è' | 'ë' | 'ê'       => 'e'
        case 'í' | 'ì' | 'ï' | 'î'       => 'i'
        case 'ó' | 'ò' | 'ö' | 'ô' | 'ø' => 'o'
        case 'ú' | 'ù' | 'ü' | 'û'       => 'u'
        case 'ç'                         => 'c'
        case 'ñ'                         => 'n'
      }
  }

  def getPage(url: String): Document = {
    val browser = JsoupBrowser()
    browser.get(url)
  }

  def getArtistSection(document: Document): List[Element] = {
    document >> elementList(".section_artist_info .artist_info")
  }

  def getDiscography(document: Document): List[Element] = {
    document >> elementList("#disco_type_s")
  }

  def getHeaders(element: Element): List[String] = element >> elementList(".info_hdr") >> text(".info_hdr")

  def getDetails(element: Element): List[Element] = element >> elementList(".info_content")

  def getRawMembers(element: Element): String = element >> text(".info_content")

  def process: Map[String, Element] = {
    val url = "/Users/timotheeaupetit/Library/Preferences/IntelliJIdea2018.1/scratches/APC.html"
    val document = getPage(url)

    val info = getArtistSection(document)

    val headers = getHeaders(info.head)

    val details = getDetails(info.head)

    headers.zip(details).toMap
  }
}

object Scraper {
  def getMembers(document: Document): List[Person] = ???
  def getBand: Band = ???
}
