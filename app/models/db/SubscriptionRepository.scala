package models.db

import javax.inject.Inject

import models.entity.Subscription
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future


class SubscriptionRepository @Inject()(protected val tables: Tables,
                                       protected val dbConfigProvider: DatabaseConfigProvider) {


  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  val Subscriptions = TableQuery[tables.SubscriptionTable]

  def getAll(): Future[Seq[Subscription]] = db.run {
    Subscriptions.result
  }

  def getByManga(mangaId: Int): Future[Option[Subscription]] = db.run {
    Subscriptions.filter(s => s.mangaId === mangaId).result.headOption
  }
  def getByMangaAndUser(mangaId: Int, userId: Int):Future[Option[(String, String)]] = {
    //    Subscriptions.filter(s => s.mangaId === mangaId && s.userId === userId).returning().map(_.)
    val Mangas = TableQuery[tables.MangaTable]
    val Users = TableQuery[tables.UserTable]

    val query: Query[(Rep[String], Rep[String]), (String, String), Seq] = for {
      (s1, m) <- Subscriptions join Mangas on (_.mangaId === _.id)
      (s2, u) <- Subscriptions join Users on (_.userId === _.id)
    } yield (m.title, u.email)

    val r: Future[Option[(String, String)]] = db.run {
      query.result.headOption
    }
    r
  }

  def insert(subscription: Subscription) = db.run {
    Subscriptions += subscription
  }

}

