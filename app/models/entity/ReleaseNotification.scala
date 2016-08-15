package models.entity

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by Leonardo on 19/07/2016.
  */
case class ReleaseNotification(id: Option[Int] = None, subscriptionId: Int, chapterId: Int)

case class ReleaseInfo(mangaName: String, chapterNum: String, link: String, chapterName: String, releaseDate: Date) {
  val format = "dd/MM/yyyy"

  def printableRelease = {
    val formatter = new SimpleDateFormat(format)
    formatter.format(releaseDate)
  }
}