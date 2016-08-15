package models.db

import javax.inject.Inject

import models.entity.Chapter
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Leonardo on 13/07/2016.
  */
class ChapterRepository @Inject()(protected val tables: Tables,
                                  protected val dbConfigProvider: DatabaseConfigProvider) {


  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  val db = dbConfig.db

  import dbConfig.driver.api._

  private val Chapters = TableQuery[tables.ChapterTable]

  def getByNameAndNum(name: String, num: String): Future[Option[Chapter]] = db.run {
    Chapters.filter(c => c.name === name && c.numeration === num).result.headOption
  }

  def getAllChaptersAvailable(): Future[Seq[Chapter]] = db.run {
    Chapters.filter(c => c.available === true).result
  }

  def insert(chapter: Chapter) = db.run {
    (Chapters += chapter).transactionally.asTry
  }

  def insertReturningId(chapter: Chapter) (implicit ec: ExecutionContext): Future[Int] = db.run {
    ((Chapters returning Chapters.map(_.id) into ((ch, genId) => ch.copy(id = genId))) += chapter)
      .map(_.id.getOrElse(0))
  }

  def updateToNotAvailable(chapter: Chapter): Future[Int] = db.run {
    val q = for {
      c <- Chapters if c.id === chapter.id
    } yield c.available
    q.update(false)
  }

}
