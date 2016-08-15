package models.scraper


import akka.actor.{Actor, Props}

/**
  * Created by Leonardo on 13/07/2016.
  */
object MangaFinderActor {

  def props = Props[MangaFinderActor]

  case class SearchResult(mangaTitleLink: Option[List[(String, String)]])

  case object StartSearch

}

class MangaFinderActor extends Actor {

  import MangaFinderActor._

  def receive: Receive = {
    case StartSearch =>
      val allMangas: AllMangas = MSScraper.getAllMangas()
      sender ! ManagerActor.SaveFoundMangas(allMangas.mangas)
  }
}
