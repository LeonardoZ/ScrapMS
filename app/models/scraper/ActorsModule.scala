package models.scraper
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Created by Leonardo on 19/07/2016.
  */
class ActorsModule extends AbstractModule with AkkaGuiceSupport {

  def configure(): Unit = {
    bindActor[ManagerActor]("manager-actor")
    bindActorFactory[ChaptersManagerActor, ChaptersManagerActor.Factory]
    bindActorFactory[MangasUpdaterActor, MangasUpdaterActor.Factory]
    bindActorFactory[AvailableChaptersActor, AvailableChaptersActor.Factory]
    bindActorFactory[NewChapterRegisterActor, NewChapterRegisterActor.Factory]
  }
}
