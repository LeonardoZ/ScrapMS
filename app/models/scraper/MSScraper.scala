package models.scraper

import net.ruippeixotog.scalascraper.model.Element

case class AllMangas(mangas: Option[List[(String, String)]])

case class NewestReleases(releases: Option[Seq[String]])

case class MangaChapterPage(manga: Option[String], chapters: Option[List[(String, String, String)]])

/**
  * Created by Leonardo on 02/07/2016.
  */
object MSScraper {

  import net.ruippeixotog.scalascraper.browser.JsoupBrowser
  import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
  import net.ruippeixotog.scalascraper.dsl.DSL._

  private val baseUrl = "http://mangastream.com/"
  private val newestReleasedMangas = baseUrl
  private val allMangas = s"${baseUrl}/manga"
  private val specificManga = s"${baseUrl}/manga/{title}"

  private val browser = JsoupBrowser()
  private lazy val mainPage = browser.get(newestReleasedMangas)
  private lazy val documentMainPage = browser.get(newestReleasedMangas)
  private lazy val documentAllMangas = browser.get(allMangas)

  def getNewestReleases() = NewestReleases(
    documentMainPage >?> element("ul.new-list") >> elementList("li") >> attr("href")("a")
  )

  def getNewestReleases2() = {
    val lis = documentMainPage >?> element("ul.new-list li em")
  }

  def getAllMangas(): AllMangas = {
    val tbody = documentAllMangas >?> element(".table") >> element("tbody")

    val elements: Option[List[Element]] =
      tbody >> elementList("tr")

    val links = tbody >> elementList("td:nth-child(1) a")

    val result: Option[(List[String], List[String])] = for {
      tr <- elements.flatMap(x => Option(x.drop(1))) // for titles
      anchors <- links // links in table
      td <- Option(tr.map(_.children.head)) // extract first td inside row
      hrefs <- Option(anchors.map(_.attrs("href"))) // get hrefs in a
      title <- Option(td.map(_.text)) // get text from td
    } yield (title, hrefs) // tuple of manga title and its link

    result match {
      case Some(xs) => AllMangas(Option(xs._1 zip xs._2))
      case None => AllMangas(None)
    }
  }

  def getMangaChapterList(manga: String): MangaChapterPage = {
    val mangaPage = specificManga.replace("{title}", manga)
    val doc = browser.get(mangaPage) >?> element(".row-fluid")
    val mangaTitle = doc >> text("h1")

    val table = doc >> element("table") >> element("tbody")
    val chapterList: Option[List[Element]] =  table >> elementList("tr")
    val links: Option[List[Element]] = table >> elementList("a")

    val result = for {
      tr <- chapterList.flatMap(x => Option(x.drop(1))) // for titles
      anchors <- links // links in table
      td <- Option(tr.map(_.children.head)) // extract first td inside row
      td2 <- Option(tr.map(_.children.drop(1).head)) // extract second td inside row
      hrefs <- Option(anchors.map(_.attrs("href"))) // get hrefs in a
      title <- Option(td.map(_.text)) // get text from td
      date <- Option(td2.map(_.text)) // get text from td2
    } yield (title, date, hrefs)
    result match {
      case Some(xs) => MangaChapterPage(mangaTitle, Option(xs.zipped.toList))
      case None => MangaChapterPage(None, None)
    }
  }

  def verifyUrlIsInvalid(url: String): Boolean =
    browser.get(url).title == "Page Not Found - Manga Stream"


}
