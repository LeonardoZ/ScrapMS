package models.scraper

import java.sql.Date
import javax.inject.Inject

import models.db.{ChapterRepository, MangaRepository}
import models.entity.{Chapter, Manga}

import scala.collection.immutable.Seq
import scala.concurrent.Future

/**
  * Created by Leonardo on 18/07/2016.
  */
class ChapterService @Inject()(protected val mangaRepository: MangaRepository,
                               protected val chapterRepository: ChapterRepository,
                               protected val rssReader: RSSReader) {


  import scala.concurrent.ExecutionContext.Implicits.global

  def getNewChapters: Future[Seq[Chapter]] = Future.sequence {
    (rssReader getChaptersFromRss) map feedToRealChapter
  }

  private def feedToRealChapter(feed: ChapterFeed) = {
    val titleAndNum = feed.extractTitleAndNumber
    val manga: Future[Option[Manga]] = mangaRepository.getByName(titleAndNum._1)
     manga.flatMap({
      case Some(m) => createfromFeed(m, titleAndNum._2, feed)
      case None => Future(Chapter(id = None, name = "", link = "", numeration = "", releaseDate = null))
    })
  }

  private def createfromFeed(manga: Manga, num: String, feed: ChapterFeed): Future[Chapter] = {
    val ch = Chapter(name = feed.description,
      link = feed.link,
      numeration = num,
      releaseDate = new Date(feed.publicationDate.getTime),
      mangaId = manga.id.get)

    chapterRepository.getByNameAndNum(ch.name, ch.numeration).flatMap({
      case Some(x) =>
        Future {
          x.copy(id = None)
        }
      case None =>
        chapterRepository.insertReturningId(ch) map { id => ch.copy(id = Some(id)) }
    })
  }

}
