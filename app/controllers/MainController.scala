package controllers

import javax.inject.Inject

import models.db.{ReleaseNotificationRepository, SubscriptionRepository}
import models.entity.{Chapter, Manga, ReleaseInfo, ReleaseNotification}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Leonardo on 20/07/2016.
  */
class MainController @Inject()(val subscriptionRepository: SubscriptionRepository,
                               val releaseNotificationRepository: ReleaseNotificationRepository,
                               val messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends Controller with I18nSupport {


  def index() = SecureRequest.async { implicit request =>
    val result: Future[Seq[((ReleaseNotification, Chapter), Manga)]] =
      releaseNotificationRepository.getReleasesOfUser(request.userEmail)
    val releasesF: Future[Seq[ReleaseInfo]] = result.map(_.map(createReleaseInfo))
    releasesF.flatMap(releases => Future(Ok(views.html.index(releases))))
  }

  def createReleaseInfo(releaseCheapterManga: ((ReleaseNotification, Chapter), Manga)): ReleaseInfo = {
    val chapterVal = releaseCheapterManga._1._2.name
    val numerationVal = releaseCheapterManga._1._2.numeration
    val linkVal = releaseCheapterManga._1._2.link
    val releaseVal = releaseCheapterManga._1._2.releaseDate
    val mangaVal = releaseCheapterManga._2.title

    ReleaseInfo(
      chapterName = chapterVal,
      link = linkVal,
      mangaName = mangaVal,
      chapterNum = numerationVal,
      releaseDate = releaseVal)
  }
}
