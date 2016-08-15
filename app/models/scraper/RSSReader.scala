package models.scraper

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import scala.collection.immutable.Seq
import scala.xml.{Elem, Node, NodeSeq, XML}

/**
  * Created by Leonardo on 18/07/2016.
  */

case class ChapterFeed(title: String, link: String, description: String, publicationDate: Date) {
  val chapterNumPattern = "(\\s(\\w|\\.)+$|(\\s\\d+\\s\\/\\s\\d+))".r
  def extractTitleAndNumber: (String, String) = {
    chapterNumPattern findFirstIn title
      match {
      case Some(num) => (title replaceAll(num, ""), num.drop(1))
      case None => (title, "Num not found")
    }
  }
}

class RSSReader {
  val rssDatePattern = "EEE, dd MMM yyyy HH:mm:ss zzz"
  val xml: Elem = XML.load("http://mangastream.com/rss")

  def getChaptersFromRss: Seq[ChapterFeed] = {
    val sdf = new SimpleDateFormat(rssDatePattern, Locale.ENGLISH)
    val items: NodeSeq = xml \\ "item"
    items.map(node => withChapterConverter(sdf, node))
  }

  def withChapterConverter(sdf: SimpleDateFormat,node: Node):
    ChapterFeed = ChapterFeed(title = (node \\ "title").text,
      link = (node \\ "link").text,
      description = (node \\ "description").text,
      publicationDate = sdf.parse((node \\ "pubDate").text))
}
