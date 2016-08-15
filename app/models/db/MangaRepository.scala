package models.db

import javax.inject.Inject

import models.entity.{Manga, Subscription, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future


class MangaRepository @Inject()(protected val tables: Tables,
                                protected val dbConfigProvider: DatabaseConfigProvider) {


  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  val Mangas = TableQuery[tables.MangaTable]

  def getById(mangaId: Int): Future[Option[Manga]] = db.run {
    Mangas.filter(_.id === mangaId).result.headOption
  }

  def getByName(mangaName: String): Future[Option[Manga]] = db.run {
    Mangas.filter(_.title === mangaName).result.headOption
  }


  def getAll(): Future[Seq[Manga]] = db.run {
    Mangas.result
  }

  def saveAll(mangaTitleLinks: Option[List[(String, String)]]) = db.run {
    val obtainedMangas: Option[List[Manga]] =
      mangaTitleLinks
        .map{ tuples =>
          tuples.map(y => Manga(id = None,title = y._1, link = y._2))
        }
    val newMangas = obtainedMangas.get
    Mangas ++= newMangas
  }

  def allMangasWithSubscriptionsOf(user: User): Future[Seq[(Manga, Option[Subscription])]] = {
    val Subscriptions = TableQuery[tables.SubscriptionTable]

    val q = Mangas joinLeft Subscriptions on((m, s) => m.id === s.mangaId && s.userId === user.id)

    db.run {
      q.result
    }

  }

}

