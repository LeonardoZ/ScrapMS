package models.db

import javax.inject.Inject

import models.entity.{Chapter, Manga, ReleaseNotification}
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by Leonardo on 13/07/2016.
  */
class ReleaseNotificationRepository @Inject()(protected val tables: Tables,
                                              protected val dbConfigProvider: DatabaseConfigProvider) {


  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  val db = dbConfig.db

  import dbConfig.driver.api._

  private val ReleaseNotifications = TableQuery[tables.ReleaseNotificationTable]

  def insert(releaseNotification: ReleaseNotification) = db.run {
    (ReleaseNotifications += releaseNotification)
  }

  def getReleasesOfUser(email: String): Future[Seq[((ReleaseNotification, Chapter), Manga)]] = {
    val Chapters = TableQuery[tables.ChapterTable]
    val Mangas = TableQuery[tables.MangaTable]

    val q = ReleaseNotifications join Chapters on (_.chapterId === _.id) join Mangas on (_._2.mangaId === _.id)


    db.run {
      q.result
    }
  }

}
