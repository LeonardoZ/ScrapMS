package models.scraper

import javax.inject.Inject

import akka.actor.Actor
import akka.event.LoggingReceive
import models.db.ChapterRepository
import models.entity.Chapter

import scala.concurrent.Future

/**
  * Created by Leonardo on 19/07/2016.
  */
object AvailableChaptersActor {

  trait Factory {
    def apply(): Actor
  }

  case object StartVerification
}

class AvailableChaptersActor @Inject()(protected val chapterRepository: ChapterRepository) extends Actor {

  import AvailableChaptersActor._

  import scala.concurrent.ExecutionContext.Implicits.global

  val scraper = MSScraper

  def receive: Receive = LoggingReceive {
    case StartVerification => {
      val fsChapters = chapterRepository.getAllChaptersAvailable()
      val x = for {
        chapters: Seq[Chapter] <- fsChapters
        sv <- Future.sequence(chapters.map(checkChapter))
      } yield sv
    }
  }

  def checkChapter(chapter: Chapter): Future[Chapter] = {
    if (scraper.verifyUrlIsInvalid(chapter.link))
      updateChapter(chapter)
    else
      Future.successful(chapter)

  }

  def updateChapter(chapter: Chapter): Future[Chapter] = {
    val affected = chapterRepository.updateToNotAvailable(chapter)
    affected.map(rows => {
      // updated means it's not available
      val updated = rows > 0
      chapter.copy(available = updated)
    })
  }
}
