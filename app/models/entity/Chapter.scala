package models.entity

import java.sql.Date

/**
  * Created by Leonardo on 13/07/2016.
  */
trait BaseChapter

case class Chapter(id: Option[Int] = None,
                   name: String,
                   numeration: String,
                   link: String,
                   releaseDate: Date,
                   available: Boolean = true,
                   mangaId: Int = 0) extends BaseChapter

class NoChapter extends BaseChapter



