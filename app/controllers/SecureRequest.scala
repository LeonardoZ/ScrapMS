package controllers

import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}

import scala.concurrent.Future


/**
  * Created by Leonardo on 12/07/2016.
  */
class AuthenticatedRequest[A](val userEmail: String, request: Request[A]) extends WrappedRequest[A](request)

object SecureRequest extends ActionBuilder[AuthenticatedRequest] {

  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] =
    request.session.get("logged-user") match {
      case Some(userEmail) =>{ block(new AuthenticatedRequest[A](userEmail, request))}
      case None => Future.successful(Redirect(routes.Application.signin()))
    }
}
