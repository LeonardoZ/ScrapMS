package models.entity

/**
  * Created by Leonardo on 19/07/2016.
  */
case class Subscription(id: Option[Int] = None, mangaId: Int, userId: Int)

trait SubscriptionReturn

case class SubscriptionSuccess(manga: String, user: String)
