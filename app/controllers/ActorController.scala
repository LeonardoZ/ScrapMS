package controllers

import javax.inject.{Inject, Named}

import akka.actor.ActorSystem
import com.google.inject.Singleton
import models.scraper.ManagerActor
import play.api.mvc.Controller



@Singleton
class ActorController @Inject()(@Named("manager-actor") managerActor: ManagerActor)(system: ActorSystem)
  extends Controller {

//  val managerActor = system.actorOf(ManagerActor.props, "manager-actor")



}
