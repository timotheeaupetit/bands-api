package com.music.scraper

import com.music.api.utils.ProjectConfiguration._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

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

  def getPageContent(url: String) = {
    val browser = JsoupBrowser()
    browser.get(url)
  }
}
