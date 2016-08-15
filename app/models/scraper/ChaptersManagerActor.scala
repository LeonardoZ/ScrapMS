package models.scraper

import javax.inject.Inject

import akka.actor.Actor
import akka.event.LoggingReceive
import models.entity.Chapter
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.InjectedActorSupport

import scala.collection.immutable.Seq
import scala.concurrent.Future
import scala.concurrent.duration._

object ChaptersManagerActor {

  trait Factory {
    def apply(): Actor
  }

  object StartChapterSearch

}

class ChaptersManagerActor @Inject()(protected val newChapterFactory: NewChapterRegisterActor.Factory,
                                     protected val chapterService: ChapterService)
  extends Actor with InjectedActorSupport {

  import ChaptersManagerActor._

  val scraper = MSScraper;

  def receive: Receive = LoggingReceive {
    case StartChapterSearch => {
      val newestReleases: Future[Seq[Chapter]] = chapterService.getNewChapters
      println(newestReleases)
      newestReleases.map(_ filter (c => c.id.isDefined) map childForNewChapter)
    }
  }

  def childForNewChapter(chapter: Chapter)= {
    implicit val timeout = 40 seconds;
    val newChapterActor = injectedChild(newChapterFactory(),
      s"new-chapter-register-${chapter.numeration + chapter.mangaId}" + System.nanoTime())
    newChapterActor ! NewChapterRegisterActor.NewChapter(chapter)
  }

}
