package services

import javax.inject.{Inject, Singleton}
import slick.repositories.UserRepository
import play.api.mvc.RequestHeader
import slick.models.User
import mvc.AuthenticateHelpers
import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class AuthenticateService @Inject() (
  userRepository: UserRepository
) extends AuthenticateHelpers {

  // FIXME: methodではなくクラス側にexecutionContextを持たせた方が良いかも
  def authenticate(request: RequestHeader)(implicit ec: ExecutionContext): Future[Option[User]] = {
    request.session.get(SESSION_ID) match {
      case Some(sid) if Try(sid.toLong).isSuccess =>
        for {
          userOpt <- userRepository.findById(sid.toLong)
        } yield userOpt
      case None      =>
        Future.successful(None)
    }
  }
}
