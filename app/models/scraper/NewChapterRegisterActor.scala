package models.scraper

import javax.inject.Inject

import akka.actor.Actor
import akka.event.LoggingReceive
import models.db.{MangaRepository, ReleaseNotificationRepository, SubscriptionRepository}
import models.entity.{Chapter, ReleaseNotification}
import models.scraper.NewChapterRegisterActor.NewChapter
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * Created by Leonardo on 22/07/2016.
  */
object NewChapterRegisterActor {

  trait Factory {
    def apply(): Actor
  }

  case class NewChapter(chapter: Chapter)

}

class NewChapterRegisterActor @Inject()(val releaseRepository: ReleaseNotificationRepository,
                                        val subscriptionRepository: SubscriptionRepository,
                                        val mangaRepository: MangaRepository) extends Actor {

  def receive: Receive = LoggingReceive {
    case NewChapter(nchapter) => {
      println(nchapter)
      val subscriptions = subscriptionRepository.getByManga(nchapter.mangaId)
      println(subscriptions)
      subscriptions.flatMap({
        case Some(sub) => {
          val release = ReleaseNotification(
            subscriptionId = sub.id.getOrElse(0),
            chapterId = nchapter.id.getOrElse(0))
          releaseRepository.insert(release) map( insert => {
            // Email and chapter?
          })
        }
        case None => Future {
          None
        }
      })
    }
  }

}
