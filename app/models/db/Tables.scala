package models.db

import java.sql.Date
import javax.inject.{Inject, Singleton}

import models.entity._
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Leonardo on 16/07/2016.
  * Could be divided by domain logic models, for organization.
  */

object Tables {

}

@Singleton
class Tables @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Option[Int]]("id", O.AutoInc, O.PrimaryKey)

    def email = column[String]("email")

    def password = column[String]("password")

    def remember = column[Boolean]("remember")

    def * = (id, email, password, remember) <> (User.tupled, User.unapply)
  }

  class MangaTable(tag: Tag) extends Table[Manga](tag, "manga") {

    def id = column[Option[Int]]("id", O.AutoInc, O.PrimaryKey)

    def title = column[String]("title", O.Length(200))

    def link = column[String]("link", O.Length(255))

    def * = (id, title, link) <> (Manga.tupled, Manga.unapply)
  }

  class ChapterTable(tag: Tag) extends Table[Chapter](tag, "chapter") {

    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name", O.Length(200))

    def numeration = column[String]("numeration", O.Length(25))

    def link = column[String]("link", O.Length(255))

    def releaseDate = column[Date]("release_date")

    def available = column[Boolean]("available")

    def mangaId = column[Int]("manga_id")

    def manga = foreignKey("chapter_ibfk_1", mangaId, TableQuery[MangaTable])(_.id.get)

    override def * =
      (id, name, numeration, link, releaseDate, available, mangaId) <> (Chapter.tupled, Chapter.unapply)

  }


  class SubscriptionTable(tag: Tag) extends Table[Subscription](tag, "subscription") {

    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Int]("user_id")

    def mangaId = column[Int]("manga_id")

    def manga = foreignKey("subscription_ibfk_1", mangaId, TableQuery[MangaTable])(_.id.get)

    def user = foreignKey("subscription_ibfk_2", userId, TableQuery[UserTable])(_.id.get)

    override def * =
      (id, mangaId, userId) <> (Subscription.tupled, Subscription.unapply)

  }

  class ReleaseNotificationTable(tag: Tag) extends Table[ReleaseNotification](tag, "release_notification") {

    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def subscriptionId = column[Int]("subscription_id")

    def chapterId = column[Int]("chapter_id")

    def subscription = foreignKey("release_notification_ibfk_1", subscriptionId, TableQuery[SubscriptionTable])(_.id.get)

    def chapter = foreignKey("release_notification_ibfk_2", chapterId, TableQuery[ChapterTable])(_.id.get)

    override def * =
      (id, subscriptionId, chapterId) <> (ReleaseNotification.tupled, ReleaseNotification.unapply)

  }


}
