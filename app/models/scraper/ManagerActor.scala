package models.scraper

import javax.inject.Inject

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.duration._

object ManagerActor {

  def props = Props[ManagerActor]

  case class SaveFoundMangas(mangasTitleLink: Option[List[(String, String)]])

  case class MangasSaved(msg: String = "Ok")

  case object SearchNewMangas

  case object SearchNewChapters

  case object VerifyChaptersAvailable

}

class ManagerActor @Inject()(val chaptersFactory: ChaptersManagerActor.Factory,
                             val mangasUpdaterFactory: MangasUpdaterActor.Factory,
                             val availableFactory: AvailableChaptersActor.Factory)

  extends Actor with InjectedActorSupport {

  import ManagerActor._

  implicit val timeout: Timeout = 50 seconds

  override def preStart() {
    import scala.concurrent.duration._
    context.system.scheduler.schedule(0 minutes, 30 minutes, this.self, SearchNewMangas)
    context.system.scheduler.schedule(0 minutes, 35 seconds, this.self, SearchNewChapters)
    context.system.scheduler.schedule(0 minutes, 20 minutes, this.self, VerifyChaptersAvailable)
  }

  def receive: Receive = LoggingReceive {

    case SearchNewMangas => {
      val finderActor = context.actorOf(MangaFinderActor.props)
      finderActor ! MangaFinderActor.StartSearch
    }

    case SaveFoundMangas(mangas) => {
      implicit val timeout = 50 seconds
      val mangasUpdaterActor = injectedChild(mangasUpdaterFactory(), "manga-updater-actor$" + System.nanoTime())
      mangasUpdaterActor ! MangasUpdaterActor.SaveMangaList(mangas)
    }

    case SearchNewChapters => {
      implicit val timeout = 40 seconds
      val chaptersManagerActor = injectedChild(chaptersFactory(), "chapters-manager-actor$" + System.nanoTime())
      chaptersManagerActor ! ChaptersManagerActor.StartChapterSearch
    }

    case VerifyChaptersAvailable => {
      implicit val timeout = 40 seconds
      val availableActor = injectedChild(availableFactory(), "available-chapters-actor$" + System.nanoTime())
      availableActor ! AvailableChaptersActor.StartVerification
    }
  }


}
