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
import play.api.mvc.Result
import play.api.mvc.Results

@Singleton
class AuthenticateService @Inject() (
  userRepository: UserRepository
) extends AuthenticateHelpers
  with    Results {

  /**
   * 認証成功時にはユーザを失敗時にはエラーのResultを返す
   */
  def authenticate(request: RequestHeader)(implicit ec: ExecutionContext): Future[Either[Result, User]] = {
    request.session.get(SESSION_ID) match {
      case Some(sid) if Try(sid.toLong).isSuccess =>
        for {
          userOpt <- userRepository.findById(sid.toLong)
        } yield {
          userOpt match {
            case None       => Left(Redirect("/login"))
            case Some(user) => Right(user)
          }
        }
      case _      =>
        Future.successful(Left(Redirect("/login")))
    }
  }

  /**
    * 認証状態に合わせてOption[User]を返す
    */
  def authenticateOrNot(request: RequestHeader)(implicit ec: ExecutionContext): Future[Option[User]] = {
    request.session.get(SESSION_ID) match {
      case Some(sid) if Try(sid.toLong).isSuccess =>
        for {
          userOpt <- userRepository.findById(sid.toLong)
        } yield userOpt
      case _      =>
        Future.successful(None)
    }
  }

}
