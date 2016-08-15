package controllers

import javax.inject.Inject

import models.db.{MangaRepository, UserRepository}
import models.entity.{Manga, Subscription, User}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Leonardo on 30/06/2016.
  */
class MangasController @Inject()(val mangaRepo: MangaRepository,
                                 val userRepo: UserRepository,
                                 val messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext)
  extends Controller with I18nSupport {

  case class MangaSubscribed(manga: Manga, subscribed: Option[Subscription])

  def list = SecureRequest.async { implicit request =>

    val userF: Future[Option[User]] = userRepo.getUser(request.userEmail)
      val mangas: Future[Seq[(Manga, Option[Subscription])]] = userF.flatMap({
        case Some(u) => mangaRepo.allMangasWithSubscriptionsOf(u)
        case None => Future { Seq()}
      })


      mangas.flatMap(seq => {println(seq);  Future { Ok(views.html.manga_list(seq))}})
  }

  def listChapters = Action {
    Ok(views.html.chapters())
  }

}
