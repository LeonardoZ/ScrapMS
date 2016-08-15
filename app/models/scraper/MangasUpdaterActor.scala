package models.scraper

import javax.inject.Inject

import akka.actor.Actor
import models.db.MangaRepository
import models.entity.Manga

import scala.concurrent.Future

/**
  * Created by Leonardo on 13/07/2016.
  */

object MangasUpdaterActor {

  trait Factory {
    def apply(): Actor
  }

  case class SaveMangaList(mangas: Option[List[(String, String)]])

}

class MangasUpdaterActor @Inject()(protected val mangaRepo: MangaRepository) extends Actor {

  import MangasUpdaterActor._

  import scala.concurrent.ExecutionContext.Implicits.global


  def receive: Receive = {
    case SaveMangaList(aBunchOfMangas) =>

      val allMangas: Future[Seq[Manga]] = mangaRepo.getAll()
      // Mangas in repository to tuples for comp with diff
      val ts: Future[List[(String, String)]] = allMangas.map { ms => ms.map { m => (m.title, m.link) }.toList }
      // All diff mangas
      val y: Future[Option[List[(String, String)]]] = ts.map(x => aBunchOfMangas.map(z => z.diff(x)))
      // saving then all
      y.flatMap(mangaRepo.saveAll(_))

      sender() ! ManagerActor.MangasSaved
  }
}
