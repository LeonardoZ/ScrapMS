package controllers

import javax.inject.Inject

import models.db.{MangaRepository, SubscriptionRepository, UserRepository}
import models.entity.{Manga, Subscription, SubscriptionSuccess, User}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Leonardo on 30/06/2016.
  */
class SubscriptionsController @Inject()(val subscriptionRepository: SubscriptionRepository,
                                        val mangaRepository: MangaRepository,
                                        val userRepository: UserRepository,
                                        val messagesApi: MessagesApi)
                                       (implicit ec: ExecutionContext)
  extends Controller with I18nSupport {

  implicit val subscriptionSuccess: Writes[SubscriptionSuccess] = Json.writes[SubscriptionSuccess]

  implicit val subscribeReads: Reads[String] =
    ((JsPath \ "mangaId").read[String])

  def subscribe = SecureRequest.async(BodyParsers.parse.json) { request =>
    println("Body is" + request.body)
    val sub: JsResult[String] = request.body.validate[String](subscribeReads)
    sub.fold(
      error => Future {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(error)))
      },
      mangaId => {
        doSubscription(Integer.valueOf(mangaId), request.userEmail)
      }
    )
  }

  private def doSubscription(mangaId: Int, email: String): Future[Result] = {
    val aUser = userRepository.getUser(email)
    val aManga = mangaRepository.getById(mangaId)
    val aSubscription: Future[Option[Subscription]] = for {
      user: Option[User] <- aUser
      manga: Option[Manga] <- aManga
      sub <- Future(createSubcription(user, manga))
    } yield (sub)
    val result = aSubscription.flatMap({
      case Some(sub) => {
        val insertF = subscriptionRepository.insert(sub)
        val aMangaAndUserF = subscriptionRepository.getByMangaAndUser(sub.mangaId, sub.userId)
        aMangaAndUserF.flatMap({
          case Some(mangaUser) => Future {Ok(Json.toJson(SubscriptionSuccess(mangaUser._1, mangaUser._2)))}
          case None => Future {BadRequest}
        })
      }
      case None => Future.successful(BadRequest)
    })
    result
  }

  def createSubcription(aUser: Option[User], aManga: Option[Manga]) =
    for {
      user: User <- aUser if aUser.isDefined
      manga: Manga <- aManga if aManga.isDefined
      sub <- Option(Subscription(userId = user.id.get, mangaId = manga.id.get))
    } yield sub

}
